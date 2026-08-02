package com.chc.ai.config;

import com.chc.ai.tool.StudentTool;
import com.chc.ai.tool.WeatherTool;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Configuration
public class ChatClientConfig {
    @Resource
    private ChatClient.Builder chatClientBuilder;
    @Resource
    private StudentTool studentTool;
    @Resource
    private WeatherTool weatherTool;
    @Bean
    public ChatClient chatClient() {
        return chatClientBuilder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                // 告诉大模型有哪些工具,把pojo对象通过 ToolCallbacks.from() 转为 ToolCallback
                .defaultTools(studentTool,weatherTool)
                .build();
    }
}
