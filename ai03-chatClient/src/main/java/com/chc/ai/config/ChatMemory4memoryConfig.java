package com.chc.ai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.model.chat.memory.autoconfigure.ChatMemoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Configuration
public class ChatMemory4memoryConfig {
    @Bean
    public ChatMemoryRepository inMemoryChatMemoryRepository() {
        return new InMemoryChatMemoryRepository();
    }
    /**
     * 自定义{@link ChatMemory},默认的在{@link ChatMemoryAutoConfiguration}中注入{@link MessageWindowChatMemory}
     * 依赖的底层存储默认实现是{@link InMemoryChatMemoryRepository}
     */
    @Bean
    public ChatMemory inMemoryChatMemory() {
        return MessageWindowChatMemory.builder()
                // 指定上下文长度
                .maxMessages(10)
                .chatMemoryRepository(inMemoryChatMemoryRepository()).build();
    }
}
