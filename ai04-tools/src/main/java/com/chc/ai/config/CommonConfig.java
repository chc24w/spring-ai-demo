package com.chc.ai.config;

import com.chc.ai.ex.handler.ExHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Configuration
public class CommonConfig {
    @Bean
    public ExHandler exHandler() {
        return new ExHandler();
    }
}
