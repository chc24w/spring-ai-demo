package com.chc.ai.service;

import com.alibaba.cloud.ai.autoconfigure.dashscope.DashScopeRerankAutoConfiguration;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import com.alibaba.cloud.ai.document.DocumentWithScore;
import com.alibaba.cloud.ai.model.RerankModel;
import com.alibaba.cloud.ai.model.RerankRequest;
import com.alibaba.cloud.ai.model.RerankResponse;
import jakarta.annotation.Resource;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.document.Document;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Service
public class RerankModelService {
    /**
     * {@link DashScopeRerankModel}在{@link DashScopeRerankAutoConfiguration}中完成注入
     */
    @Resource
    private RerankModel rerankModel;
    /**
     * 测试重排序模型
     */
    public List<Document> rerank(String input, List<Document> documents) {
        RerankRequest rerankRequest = new RerankRequest(input, documents);
        // 指定精排模型,精排默认使用前topN=5
        RerankResponse response = rerankModel.call(rerankRequest);
        // 排序后的docs
        List<Document> rankedDocs = response.getResults()
                .stream()
                .filter(doc -> doc != null && doc.getScore() >= 0.1d)
                .sorted(Comparator.comparingDouble(DocumentWithScore::getScore).reversed())
                .map(DocumentWithScore::getOutput)
                .toList();
        Usage usage = response.getMetadata().getUsage();
        // promptTokens = 0
        System.out.println("promptTokens = " + usage.getPromptTokens());
        // completionTokens = 0
        System.out.println("completionTokens = " + usage.getCompletionTokens());
        // promptTokens + completionTokens
        // totalTokens = 241
        System.out.println("totalTokens = " + usage.getTotalTokens());
        return rankedDocs;
    }
}
