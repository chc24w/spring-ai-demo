package com.chc.ai.controller.audio;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechModel;
import com.alibaba.cloud.ai.dashscope.audio.tts.DashScopeAudioSpeechOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 文本转语音模型，
 */
@RestController
@Slf4j
@RequestMapping("/dashScope/textToSpeechModel")
public class DashScopeTtsController {

    /**
     * @see DashScopeAudioSpeechModel
     */
    @Resource
    private TextToSpeechModel textToSpeechModel;

    /**
     * 文本转语音
     * http://localhost:8080/dashScope/textToSpeechModel/tts?input=余额仅剩1个小目标了
     */
    @GetMapping("/tts")
    public String tts(@RequestParam("input") String input) {
        DashScopeAudioSpeechOptions options = DashScopeAudioSpeechOptions.builder()
                // 人声
                .voice("longanhuan_v3")
                // 语速
                .speed(0.8d)
                .format("wav")
                .build();
        // 只支持流式的
        Flux<TextToSpeechResponse> flux = textToSpeechModel.stream(new TextToSpeechPrompt(input, options));

        Mono<byte[]> audioMono = flux
                .map(response -> response.getResult().getOutput())
                .reduce(new byte[0], (acc, chunk) -> {
                    byte[] merged = new byte[acc.length + chunk.length];
                    System.arraycopy(acc, 0, merged, 0, acc.length);
                    System.arraycopy(chunk, 0, merged, acc.length, chunk.length);
                    return merged;
                });
        // 语音文件的字节流
        byte[] array = audioMono.block();
        // d:\a\c6f6caf257654b0db38e3a187d5b1164.mp3
        String path = "d:\\tempAI\\" + IdUtil.simpleUUID() + ".mp3";
        FileUtil.writeBytes(array,path);
        return path;
    }
}
