package com.chc.ai.chat;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.openai.autoconfigure.OpenAiChatAutoConfiguration;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestController
@Slf4j
@RequestMapping("/openai")
public class OpenaiController {
    /**
     * 在{@link OpenAiChatAutoConfiguration}
     * 中自动注入
     */
    @Resource
    private OpenAiChatModel openAiChatModel;

    /**
     * 流式聊天(设置流式的编码,不然可能会乱码)
     * http://localhost:8080/openai/stream?input=你是谁
     */
    @GetMapping(value = "/stream", produces = "text/plain;charset=UTF-8")
    public Flux<String> stream(@RequestParam("input") String input) {
        return openAiChatModel.stream(input);
    }
    /**
     * 同步请求
     * http://localhost:8080/openai/call?input=你是谁
     */
    @GetMapping("/call")
    public String call(@RequestParam("input") String input) {
        ChatResponse response = openAiChatModel.call(new Prompt(input));
        AssistantMessage assistantMessage = response.getResult().getOutput();
        String reasoning = assistantMessage.getMetadata().get("reasoningContent").toString();
        System.out.println("推理过程:" + reasoning);
        System.out.println("--------------------------");
        String res = assistantMessage.getText();
        System.out.println("最终结果:" + res);
        log.info("call res:{}",res);
        return res;
    }
}
