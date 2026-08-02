package com.chc.ai.controller;

import cn.hutool.core.util.StrUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.api.BaseChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 基于内存进行实现对话记忆
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestController
@Slf4j
@RequestMapping("/chatMemory/memory")
public class ChatMemoryByMemoryController {

    @Resource
    private ChatClient chatClient;

    /**
     * 默认上下文长度是{@link MessageWindowChatMemory#DEFAULT_MAX_MESSAGES},即最多取10轮
     * 对话的记录,自动注入依赖 org.springframework.ai:spring-ai-autoconfigure-model-chat-memory
     */
    @Resource
    private ChatMemory inMemoryChatMemory;

    /**
     * 利用advisor来进行实现,原理就和{@link #test2()}类似
     * @see BaseChatMemoryAdvisor 组装repository+消息窗口,实现类{@link MessageChatMemoryAdvisor}
     * http://localhost:8080/chatMemory/memory/test3
     */
    @GetMapping("/test3")
    public void testMemoryFinal() {
        String content = chatClient
                .prompt("我的名字是小红")
                // 分用户存储历史记录,实现多用户记忆隔离
                // 默认是:org.springframework.ai.chat.memory.ChatMemory.DEFAULT_CONVERSATION_ID
                // 如果不指定,所有用户的记录就在一起了
                .advisors(advisorSpec -> {
                    advisorSpec.advisors(
                            MessageChatMemoryAdvisor.builder(inMemoryChatMemory).build()
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
                                    MessageChatMemoryAdvisor.builder(inMemoryChatMemory).build()
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
                                    MessageChatMemoryAdvisor.builder(inMemoryChatMemory).build()
                            )
                            .param(ChatMemory.CONVERSATION_ID,"user2");
                })
                .call()
                .content();
        // 区分了不同的人的聊天记录
        System.out.println("第三次返回:"+content);
    }

    /**
     * 可以通过记录所有的输入+响应内容,然后每次都给大模型allMessage,但需要自己维护
     * 利用{@link ChatMemory},但这里需要自己进行存储管理,也是麻烦
     * http://localhost:8080/chatMemory/memory/test2
     */
    @GetMapping("/test2")
    public void test2() {
        // 多轮对话的实现依赖大模型的记忆功能
        ChatMemory chatMemory = MessageWindowChatMemory.builder()
                // 默认20条
                .maxMessages(10)
                .build();
        // 会话id
        String conversationId = "user1";
        UserMessage userMessage1 = new UserMessage("我的名字是小红");
        chatMemory.add(conversationId, userMessage1);
        String content = chatClient
                // 从chatMemory中获取
                .prompt(new Prompt(chatMemory.get(conversationId)))
                .call()
                .content();
        if (StrUtil.isNotBlank(content)) {
            chatMemory.add(conversationId, new AssistantMessage(content));
        }
        System.out.println("第一次返回:" + content);
        UserMessage userMessage2 = new UserMessage("我的名字是什么");
        chatMemory.add(conversationId, userMessage2);
        content = chatClient
                .prompt(new Prompt(chatMemory.get(conversationId)))
                .call()
                .content();
        if (StrUtil.isNotBlank(content)) {
            chatMemory.add(conversationId, new AssistantMessage(content));
        }
        // 此时就有记忆了
        System.out.println("第二次返回:" + content);
    }

    /**
     * 大模型默认是没有记忆的
     * http://localhost:8080/chatMemory/memory/test1
     */
    @GetMapping("/test1")
    public void test1() {
        String content = chatClient.prompt()
                .user("我的名字是小红")
                .call()
                .content();
        System.out.println("第一次返回:" + content);
        content = chatClient.prompt()
                .user("我的名字是什么")
                .call()
                .content();
        // 默认是没有记忆的
        // 第二次返回:你好！我目前还不知道你的名字呢，因为这是我们的第一次对话。
        System.out.println("第二次返回:" + content);
    }
}
