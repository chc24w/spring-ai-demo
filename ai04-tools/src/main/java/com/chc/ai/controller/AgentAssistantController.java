package com.chc.ai.controller;


import com.chc.ai.res.Result;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * 智能系统助手
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestController
@RequestMapping("/agentAssistant")
@Validated
@Slf4j
public class AgentAssistantController {
    @Resource
    private ChatClient chatClient;

    /**
     * http://localhost:8080/agentAssistant/testTool?input=北京的天气怎么样
     * http://localhost:8080/agentAssistant/testTool?input=帮我查询学号为2024001004的学生信息
     */
    @GetMapping("/testTool")
    public Result<String> testTool(@RequestParam("input") String input) {
        String content = chatClient.prompt()
                .user(input)
                .call().content();
        return Result.buildSuccess(content);
    }

}
