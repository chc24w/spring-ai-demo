package com.chc.ai.controller;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基于db进行实现对话记忆
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestController
@Slf4j
@RequestMapping("/chatMemory/db")
public class ChatMemoryByDbController {

    @Resource
    private ChatClient chatClient;
    @Resource
    private ChatMemory dbChatMemory;

    /**
     * http://localhost:8080/chatMemory/db/test1
     */
    @GetMapping("/test1")
    public void test1() {
        String content = chatClient
                .prompt("我的名字是小红")
                // 分用户存储历史记录,实现多用户记忆隔离
                // 默认是:org.springframework.ai.chat.memory.ChatMemory.DEFAULT_CONVERSATION_ID
                // 如果不指定,所有用户的记录就在一起了
                .advisors(advisorSpec -> {
                    advisorSpec.advisors(
                            MessageChatMemoryAdvisor.builder(dbChatMemory).build()
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
                                    MessageChatMemoryAdvisor.builder(dbChatMemory).build()
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
                                    MessageChatMemoryAdvisor.builder(dbChatMemory).build()
                            )
                            .param(ChatMemory.CONVERSATION_ID,"user2");
                })
                .call()
                .content();
        // 区分了不同的人的聊天记录
        System.out.println("第三次返回:"+content);
    }
}
