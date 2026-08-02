package com.chc.ai.config;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.model.chat.memory.repository.jdbc.autoconfigure.JdbcChatMemoryRepositoryAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Configuration
public class ChatMemory4dbConfig {
    @Resource
    private ChatMemoryRepository jdbcChatMemoryRepository;
    /**
     * @see JdbcChatMemoryRepositoryAutoConfiguration
     */
    @Bean
    public ChatMemory dbChatMemory() {
        return MessageWindowChatMemory.builder()
                // 指定上下文长度
                .maxMessages(10)
                .chatMemoryRepository(jdbcChatMemoryRepository).build();
    }
}
