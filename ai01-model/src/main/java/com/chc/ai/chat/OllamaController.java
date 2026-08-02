package com.chc.ai.chat;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.model.ollama.autoconfigure.OllamaChatAutoConfiguration;
import org.springframework.ai.ollama.OllamaChatModel;
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
@RequestMapping("/ollama")
public class OllamaController {
    /**
     * 在{@link OllamaChatAutoConfiguration}
     * 中自动注入
     */
    @Resource
    private OllamaChatModel ollamaChatModel;

    /**
     * 流式聊天(设置流式的编码,不然可能会乱码)
     * http://localhost:8080/ollama/stream?input=你是谁
     */
    @GetMapping(value = "/stream", produces = "text/plain;charset=UTF-8")
    public Flux<String> stream(@RequestParam("input") String input) {
        // <think>xxxx</think>yyyy
        return ollamaChatModel.stream(input);
    }
    /**
     * 同步请求
     * http://localhost:8080/ollama/call?input=你是谁
     */
    @GetMapping("/call")
    public String call(@RequestParam("input") String input) {
        String res = ollamaChatModel.call(input);
        // <think>xxxx</think>yyyy
        log.info("call res:{}",res);
        return res;
    }
}
