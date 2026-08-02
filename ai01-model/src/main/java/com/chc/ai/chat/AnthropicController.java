package com.chc.ai.chat;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.anthropic.AnthropicChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestController
@Slf4j
@RequestMapping("/anthropic")
public class AnthropicController {
    /**
     * 在{@link org.springframework.ai.model.anthropic.autoconfigure.AnthropicChatAutoConfiguration}
     * 中自动注入
     */
    @Resource
    private AnthropicChatModel anthropicChatModel;

    /**
     * 同步请求
     * http://localhost:8080/anthropic/call?input=1加1等于几
     */
    @GetMapping("/call")
    public String call(@RequestParam("input") String input) {
        return anthropicChatModel.call(input);
    }
}
