package com.chc.ai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.core.io.FileSystemResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@RestController
@Slf4j
@RequestMapping("/multiModal")
public class MultiModalController {
    @Resource
    private ChatClient.Builder chatClientBuilder;

    /**
     * 多模态(数据的类型:文本、图片、音频、视频):同时处理多种数据类型
     * http://localhost:8080/multiModal/test1
     */
    @GetMapping("/test1")
    public String test1() {
        org.springframework.core.io.Resource file = new FileSystemResource("d:\\a\\a.png");
        Media media = new Media(MimeTypeUtils.IMAGE_PNG, file);
        return chatClientBuilder.build()
                .prompt()
                .options(DashScopeChatOptions.builder()
                        // 标识为多模态
                        .multiModel(true)
                        .build())
                .user(userSpec -> userSpec
                        // 文本
                        .text("识别图片内容")
                        // 图片
                        .media(media)
                )
                .call()
                .content();
    }
}
