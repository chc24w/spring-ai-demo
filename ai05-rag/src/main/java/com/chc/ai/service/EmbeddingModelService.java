package com.chc.ai.service;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeEmbeddingAutoConfiguration;
import com.alibaba.cloud.ai.dashscope.spec.DashScopeApiSpec;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.*;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Service
public class EmbeddingModelService {
    /**
     * {@link com.alibaba.cloud.ai.dashscope.embedding.text.DashScopeEmbeddingModel}
     * 在{@link DashScopeEmbeddingAutoConfiguration}中完成注入
     */
    @Resource
    private EmbeddingModel embeddingModel;
    /**
     * {@link EmbeddingModel#embed(String)}底层调用
     * {@link EmbeddingModel#call(EmbeddingRequest)}方法,可以获取token信息
     */
    public void testEmbeddingCall(String input) {
        EmbeddingOptions embeddingOptions = EmbeddingOptions.builder().build();
        EmbeddingResponse embeddingResponse = embeddingModel.call(new
                EmbeddingRequest(List.of(input),embeddingOptions));
        // 获取向量化后的结果
        float[] embed = embeddingResponse.getResults()
                .stream()
                .map(Embedding::getOutput)
                .toList()
                .iterator()
                .next();
        // embed.length = 1024
        System.out.println("embed.length = " + embed.length);
        System.out.println("Arrays.toString(embed) = " + Arrays.toString(embed));
        Usage usage = embeddingResponse.getMetadata().getUsage();
        // promptTokens = 0
        System.out.println("promptTokens = " + usage.getPromptTokens());
        // completionTokens = 0
        System.out.println("completionTokens = " + usage.getCompletionTokens());
        // promptTokens + completionTokens
        // totalTokens = 399
        System.out.println("totalTokens = " + usage.getTotalTokens());
        // 也可以用nativeUsage获取
        DashScopeApiSpec.EmbeddingUsage nativeUsage = (DashScopeApiSpec.EmbeddingUsage)usage.getNativeUsage();
        // nativeUsage.totalTokens() = 399
        System.out.println("nativeUsage.totalTokens() = " + nativeUsage.totalTokens());
    }
    /**
     * 测试向量化
     */
    public void testEmbeddingEmbed(Document document) {
        float[] embed = embeddingModel.embed(document);
        // embed.length = 1024
        System.out.println("embed.length = " + embed.length);
        System.out.println("Arrays.toString(embed) = " + Arrays.toString(embed));
    }
}
