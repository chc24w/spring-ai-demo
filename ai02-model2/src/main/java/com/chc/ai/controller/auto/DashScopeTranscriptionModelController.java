package com.chc.ai.controller.auto;


import com.alibaba.cloud.ai.dashscope.audio.transcription.*;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.audio.transcription.AudioTranscriptionResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/dashScope/transcriptionModel")
public class DashScopeTranscriptionModelController {

    /**
     * @see DashScopeAudioTranscriptionModel
     */
    @Resource
    private DashScopeAudioTranscriptionModel transcriptionModel;
    /**
     * 文本转语音
     * http://localhost:8080/dashScope/transcriptionModel/transcription
     */
    @GetMapping("/transcription")
    public void transcription() {
        DashScopeAudioTranscriptionOptions options = DashScopeAudioTranscriptionOptions
                .builder()
                .model(DashScopeModel.AudioModel.FUN_ASR.getValue())
                // 指定音频的语言
//                .languageHints()
                .build();
        List<String> fileUrls = new ArrayList<>();
        // 指定网络url地址
        fileUrls.add("https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav");
        DashScopeAudioTranscriptionPrompt prompt = new DashScopeAudioTranscriptionPrompt(options, fileUrls);
        // 此处一定要用这个构造方法,否则报错:Prompt type is not DashScopeAudioTranscriptionPrompt.
        AudioTranscriptionResponse audioTranscriptionResponse = transcriptionModel.call(prompt);
        // 获取不到,需要转为dashScope的响应才能获取到结果
//        audioTranscriptionResponse.getResult();
        DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse response = (DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse) audioTranscriptionResponse;
        List<DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse.TranscriptionResult> transcriptionResults = response.getTranscriptionResults();
        for (DashScopeAsrTranscriptionApiSpec.DashScopeAudioAsrTranscriptionResponse.TranscriptionResult transcriptionResult : transcriptionResults) {
            StringBuilder sb = new StringBuilder();
            for (DashScopeTranscriptionResponse.DashScopeAudioTranscription transcript : transcriptionResult.transcripts()) {
                sb.append(transcript.getText());
            }
            // file:https://dashscope.oss-cn-beijing.aliyuncs.com/samples/audio/paraformer/hello_world_female2.wav,content:hello world，这里是阿里巴巴语音实验室。
            System.out.println("file:" + transcriptionResult.fileUrl() +",content:" + sb.toString());
        }
    }
}
