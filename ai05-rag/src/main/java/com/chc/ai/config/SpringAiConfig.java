package com.chc.ai.config;

import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.time.Duration;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Configuration
public class SpringAiConfig {
    /**
     * 配置{@link RestClient},自动配置参见{@link RestClientAutoConfiguration}
     */
    @Bean
    public RestClientCustomizer restClientCustomizer() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings
                .defaults()
                // 调用大模型在spring ai中底层是使用restClient完成的,这里设置其超时时间,避免请求超时
                .withConnectTimeout(Duration.ofSeconds(60))
                .withReadTimeout(Duration.ofSeconds(60));
        return new RestClientCustomizer() {
            @Override
            public void customize(RestClient.Builder restClientBuilder) {
                ClientHttpRequestFactory clientHttpRequestFactory = ClientHttpRequestFactoryBuilder
                        .detect().build(settings);
                restClientBuilder.requestFactory(clientHttpRequestFactory);
            }
        };
    }
}
