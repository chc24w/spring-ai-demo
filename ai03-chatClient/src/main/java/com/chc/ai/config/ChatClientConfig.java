package com.chc.ai.config;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.model.chat.client.autoconfigure.ChatClientAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Configuration
public class ChatClientConfig {
    @Resource
    private ChatModel deepSeekChatModel;

    /**
     * 原型bean
     * @see ChatClientAutoConfiguration
     */
    @Resource
    private ChatClient.Builder chatClientBuilder;

    /**
     * 业务上定义:
     * 通用的
     */
    @Bean
    public ChatClient chatClient() {
        return chatClientBuilder
                // 加入后还需要再日志中配置级别为debug
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }
    /**
     * 专门用于需求分析,可以使用需求理解能力效果较好的模型
     */
    @Bean
    public ChatClient requirementChatClient() {
        return ChatClient.builder(deepSeekChatModel)
                // 设置默认配置
                .defaultOptions(ChatOptions.builder()
//                        .temperature(1.0d)
                )
                // 设置默认的系统提示词
                .defaultSystem("""
                        # 角色
                        你是一名专业的软件行业的产品,根据用户给出的内容深入挖掘需求
                        # 输出
                        按章节输出详细的需求文档
                        """)
                .build();
    }
    /**
     * 专门用于架构设计,可以使用架构理解能力效果较好的模型
     */
    @Bean
    public ChatClient architectureChatClient() {
        return ChatClient.builder(deepSeekChatModel)
                .defaultSystem("""
                        # 角色
                        你是一名资深的软件架构师,根据需求文档产出概要设计和详细设计
                        # 输出
                        按章节输出概要设计文档和详细设计文档
                        """)
                .build();
    }

    /**
     * 专门用于编写代码的,可以使用编码效果较好的模型
     */
    @Bean
    public ChatClient codeChatClient() {
        return ChatClient.builder(deepSeekChatModel)
                .defaultSystem("""
                        # 角色
                        你是一名资深的软件开发者,根据概要设计文档和详细设计文档编写代码
                        """)
                .build();
    }
}
