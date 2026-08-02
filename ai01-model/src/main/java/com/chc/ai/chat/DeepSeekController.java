package com.chc.ai.chat;

import cn.hutool.core.collection.ListUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.deepseek.DeepSeekAssistantMessage;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.ai.deepseek.api.DeepSeekApi;
import org.springframework.ai.deepseek.api.ResponseFormat;
import org.springframework.ai.model.deepseek.autoconfigure.DeepSeekChatProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * 本质就是发送http请求:{@link DeepSeekApi#chatCompletionEntity(DeepSeekApi.ChatCompletionRequest)}
 * 基于{@link org.springframework.web.client.RestClient}发送http请求
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestController
@Slf4j
@RequestMapping("/deepSeek")
public class DeepSeekController {
    /**
     * 在{@link org.springframework.ai.model.deepseek.autoconfigure.DeepSeekChatAutoConfiguration}
     * 中自动注入
     */
    @Resource
    private DeepSeekChatModel deepSeekChatModel;
    @Resource
    private DeepSeekChatProperties deepSeekChatProperties;

    /**
     * 测试配置选项
     * http://localhost:8080/deepSeek/testConfig?input=介绍下你自己
     */
    @GetMapping("/testConfig")
    public String testOptions(@RequestParam("input") String input) {
        // 常用配置项 https://api-docs.deepseek.com/zh-cn/api/create-chat-completion
        DeepSeekChatOptions deepSeekChatOptions = DeepSeekChatOptions
                .builder()
                // 默认即text
                .responseFormat(ResponseFormat.builder().type(ResponseFormat.Type.TEXT).build())
                // 为本次会话单独指定模型
                .model("deepseek-v4-flash")
                // 0-2之间,越大表示越有创造性
                .temperature(1.9d)
                // 配置大模型生成的最大token数(字数上限)
                .maxTokens(1024)
                // "政治":遇到敏感词就停止生产
                .stop(ListUtil.of("政治"))
                .build();
        // 提示词
        Prompt promptObj = new Prompt(input, deepSeekChatOptions);
        ChatResponse response = deepSeekChatModel.call(promptObj);
        // 获取模型返回的信息
        AssistantMessage assistantMessage = response.getResult().getOutput();
        DeepSeekAssistantMessage deepSeekAssistantMessage = (DeepSeekAssistantMessage) assistantMessage;
        System.out.println("推理过程:" + deepSeekAssistantMessage.getReasoningContent());
        System.out.println("--------------------------");
        String res = assistantMessage.getText();
        System.out.println("最终结果:" + res);
        return res;
    }
    /**
     * 流式聊天(设置流式的编码,不然可能会乱码)
     * http://localhost:8080/deepSeek/stream?input=介绍下你自己
     */
    @GetMapping(value = "/stream", produces = "text/plain;charset=UTF-8")
    public Flux<String> stream(@RequestParam("input") String input) {
        return deepSeekChatModel.stream(input);
    }
    /**
     * 同步请求
     * http://localhost:8080/deepSeek/call?input=介绍下你自己
     */
    @GetMapping("/call")
    public String call(@RequestParam("input") String input) {
        String res = deepSeekChatModel.call(input);
        log.info("call res:{}",res);
        return res;
    }
}
