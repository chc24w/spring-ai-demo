package com.chc.ai.controller;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.StrUtil;
import com.chc.ai.advisor.UseLanguageAdvisor;
import com.chc.ai.advisor.UseLanguageAdvisor2;
import com.chc.ai.advisor.UseLanguageAdvisor3;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestController
@Slf4j
@RequestMapping("/chatClient")
public class ChatClientController {
    @Resource
    private ChatClient chatClient;

    /**
     * 定制提示词模板
     * http://localhost:8080/chatClient/customPromptTpl?input=动作
     */
    @GetMapping("/customPromptTpl")
    public String customPromptTpl(@RequestParam("input") String input) {
        return chatClient
                .prompt()
                .advisors(advisorSpec -> {
                    advisorSpec.advisors(new UseLanguageAdvisor3())
                            .param("languageType","中文");
                })
                .user(promptUserSpec -> {
                    promptUserSpec.text("给我推荐3部<type>类型的电影")
                            // 这里设置的参数通过chatClientRequest.context()无法取到
                            // 仅用于提示词模板解析,可以参见
                            // org.springframework.ai.chat.client.DefaultChatClientUtils.toChatClientRequest中对提示词的渲染
                            .param("type", input);
                })
                // St:StringTemplate
                .templateRenderer(StTemplateRenderer.builder()
                        // 默认变量通过{}指定,这里可以指定分隔符
                        .startDelimiterToken('<')
                        .endDelimiterToken('>')
                        .build())
                .call()
                .content();
    }

    /**
     * 测试提示词模板动态参数2,基于param进行赋值
     * http://localhost:8080/chatClient/promptTpl2?input=你是谁
     */
    @GetMapping("/promptTpl2")
    public String promptTpl2(@RequestParam("input") String input) {
        return chatClient
                .prompt()
                // spec:specification 规范
                .advisors(advisorSpec -> {
                    advisorSpec.advisors(new UseLanguageAdvisor3())
                            // 这里设置的参数最终会赋值给chatClientRequest.context()
                            .param("languageType","英文");
                })
                .user(input)
                .call()
                .content();
    }
    /**
     * 测试提示词模板动态参数1
     * http://localhost:8080/chatClient/promptTpl1?input=1加2等于几
     */
    @GetMapping("/promptTpl1")
    public String promptTpl1(@RequestParam("input") String input) {
        return chatClient
                .prompt()
                .advisors(new UseLanguageAdvisor2("英文"))
                .user(input)
                .call()
                .content();
    }
    /**
     * 流式返回-手动控制返回信息
     * http://localhost:8080/chatClient/stream2?input=介绍下spring ai
     */
    @GetMapping(value = "/stream2", produces = "text/plain;charset=UTF-8")
    public String stream2(@RequestParam("input") String input) {
        ChatClient.StreamResponseSpec responseSpec = chatClient
                .prompt()
                .user(input)
                .stream();
        // Mono(0-1结果)、Flux(0-N结果):响应式编程,io.projectreactor:reactor-core
        Flux<ChatResponse> fluxResponse = responseSpec.chatResponse();
        // 最终的推理内容
        StringBuffer finalReasoningContent = new StringBuffer();
        // 最终的正文响应
        StringBuffer finalContent = new StringBuffer();
        // 使用 AtomicReference 来记录最新一个响应
        AtomicReference<ChatResponse> lastResponseRef = new AtomicReference<>();
        CountDownLatch latch = new CountDownLatch(1);
        fluxResponse
                .doOnNext(response -> {
                    // 记录每个响应，最后一个会被保留
                    lastResponseRef.set(response);
                    processContent(response,finalReasoningContent,finalContent);
                })
                .doOnCancel(() -> {
                    System.out.println("数据流被取消");
                    latch.countDown();
                })
                .doOnError(error -> {
                    System.out.println("发生错误: " + error.getMessage());
                    latch.countDown();
                })
                .doOnComplete(() -> {
                    System.out.println("进入doOnComplete");
                    // 从 AtomicReference 中获取最后一个响应
                    ChatResponse lastResponse = lastResponseRef.get();
                    printUsage(lastResponse);
                    latch.countDown();
                })
                .subscribe();
        try {
            boolean completed = latch.await(300, TimeUnit.SECONDS);
            if (!completed) {
                System.out.println("等待超时，流可能未完成");
            }
            System.out.println("执行完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "推理:" + finalReasoningContent + "\n正文:" + finalContent;
    }
    private void processContent(ChatResponse chatResponse, StringBuffer finalReasoningContent, StringBuffer finalContent) {
        if (chatResponse == null) {
            return;
        }
        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
        String content = assistantMessage.getText();
        // 没有检测到输出返回null
        if (StrUtil.isNotBlank(content)) {
            System.out.println("正文 = " + content);
            finalContent.append(content);
        }
        DeepSeekAssistantMessage deepSeekAssistantMessage = (DeepSeekAssistantMessage) assistantMessage;
        String reasoningContent = deepSeekAssistantMessage.getReasoningContent();
        if (StrUtil.isNotBlank(reasoningContent)) {
            System.out.println("推理 = " + reasoningContent);
            finalReasoningContent.append(reasoningContent);
        }
    }
    private void printUsage(ChatResponse chatResponse) {
        if (chatResponse == null) {
            return;
        }
        ChatResponseMetadata metadata = chatResponse.getMetadata();
        Usage usage = metadata.getUsage();
        Integer totalTokens = usage.getTotalTokens();
        // doOnComplete-promptTokens = 71
        System.out.println("promptTokens = " + usage.getPromptTokens());
        //doOnComplete-completionTokens = 116
        System.out.println("completionTokens = " + usage.getCompletionTokens());
        //doOnComplete-totalTokens = 187
        System.out.println("totalTokens = " + totalTokens);
    }
    /**
     * 流式返回
     * http://localhost:8080/chatClient/stream?input=介绍下spring ai
     */
    @GetMapping(value = "/stream", produces = "text/plain;charset=UTF-8")
    public Flux<String> stream(@RequestParam("input") String input) {
        ChatClient.StreamResponseSpec responseSpec = chatClient
                .prompt()
                .user(input)
                .stream();
        Flux<String> flux = responseSpec.content();
        return flux;
    }
    /**
     * 同步请求
     * http://localhost:8080/chatClient/sync?input=介绍下你自己
     */
    @GetMapping("/sync")
    public String sync(@RequestParam("input") String input) {
        ChatClient.CallResponseSpec responseSpec = chatClient
                .prompt()
                // SafeGuardAdvisor:处理用户提示词中的敏感词,若包含,则直接失败,不进行大模型调用
                // 默认提示:I'm unable to respond to that due to sensitive content. Could we rephrase or discuss something else?
                .advisors(new SafeGuardAdvisor(ListUtil.of("政治"),"内容包含敏感词",0)
              ,new UseLanguageAdvisor("英文"))
                // 指定系统提示词
//                .system()
                // 用户提示词
                .user(input)
                .call();
        String content = responseSpec.content();
        log.info("同步响应内容:{}",content);
        ChatResponse chatResponse = responseSpec.chatResponse();
        // 统计token使用信息
        Usage usage = chatResponse.getMetadata().getUsage();
        Integer totalTokens = usage.getTotalTokens();
        // 提示词消耗的token promptTokens = 71
        System.out.println("promptTokens = " + usage.getPromptTokens());
        // 回复消耗的token:completionTokens = 229
        System.out.println("completionTokens = " + usage.getCompletionTokens());
        // promptTokens + completionTokens = totalTokens = 300
        System.out.println("totalTokens = " + totalTokens);
        return content;
    }
}
