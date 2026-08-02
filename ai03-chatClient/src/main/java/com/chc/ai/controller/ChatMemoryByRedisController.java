package com.chc.ai.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 基于db进行实现对话记忆
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
//@RestController
@Slf4j
@RequestMapping("/chatMemory/redis")
public class ChatMemoryByRedisController {

    @Resource
    private ChatClient chatClient;
    @Resource
    private ChatMemory redisChatMemory;

    /**
     * http://localhost:8080/chatMemory/redis/test1
     */
    @GetMapping("/test1")
    public void test1() {
        String content = chatClient
                .prompt("我的名字是小红")
                .advisors(advisorSpec -> {
                    advisorSpec.advisors(
                            MessageChatMemoryAdvisor.builder(redisChatMemory).build()
                    )
                    // 放到上下文中在advisor中取出使用
                    .param(ChatMemory.CONVERSATION_ID,"user1");
                })
                .call()
                .content();
        System.out.println("第一次返回:"+content);
        content = chatClient
                .prompt("我的名字是什么")
                .advisors(advisorSpec -> {
                    advisorSpec.advisors(
                                    MessageChatMemoryAdvisor.builder(redisChatMemory).build()
                            )
                            .param(ChatMemory.CONVERSATION_ID,"user1");
                })
                .call()
                .content();
        System.out.println("第二次返回:"+content);
        content = chatClient
                .prompt("我的名字是什么")
                .advisors(advisorSpec -> {
                    advisorSpec.advisors(
                                    MessageChatMemoryAdvisor.builder(redisChatMemory).build()
                            )
                            .param(ChatMemory.CONVERSATION_ID,"user2");
                })
                .call()
                .content();
        // 区分了不同的人的聊天记录
        System.out.println("第三次返回:"+content);
    }
}
