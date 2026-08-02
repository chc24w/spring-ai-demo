package com.chc.ai.config;

import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.ChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.redis.RedisChatMemoryRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.*;

//@Configuration
public class ChatMemory4redisConfig {


    /**
     * 1、设置，上下文长度
     * @return
     */
    @Bean
    public ChatMemory redisChatMemory() {
        return MessageWindowChatMemory.builder()
                // 指定上下文长度
                .maxMessages(10)
                .chatMemoryRepository(redisChatMemoryRepository()).build();
    }

    /**
     * 2、通过jedisClient构建redisChatMemoryRepository
     * @return
     */
    @Bean
    public ChatMemoryRepository redisChatMemoryRepository() {
        return RedisChatMemoryRepository.builder()
                .jedisClient(jedisClient())
                .build();
    }

    /**
     * 3、初始化RedisClient
     * @return
     */
    @Bean
    public RedisClient jedisClient() {
        JedisClientConfig clientConfig = DefaultJedisClientConfig.builder()
                .hostAndPortMapper(new HostAndPortMapper() {
                    @Override
                    public HostAndPort getHostAndPort(HostAndPort hostAndPort) {
                        return new HostAndPort("localhost",6379);
                    }
                })
                // 连接超时(ms)
                .timeoutMillis(6000)
                .build();
        return RedisClient.builder().clientConfig(clientConfig).build();
    }
}
