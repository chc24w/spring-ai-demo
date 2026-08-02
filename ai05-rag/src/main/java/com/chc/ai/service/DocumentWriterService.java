package com.chc.ai.service;

import jakarta.annotation.Resource;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Service
public class DocumentWriterService {
    @Resource
    private VectorStore vectorStore;

    public void add(List<Document> docs) {
        // 存储,内部会调用org.springframework.ai.embedding.EmbeddingModel.embed()方法
        vectorStore.add(docs);
    }

    /**
     *
     * @param input 用户输入的内容, 如保真吗/是正品吗
     * @param userId 过滤哪个租户的文档
     */
    public List<Document> search(String input,Long userId) {
        SearchRequest.Builder queryBuilder = SearchRequest.builder().query(input);
        if (userId != null) {
            // 通过metadata进行过滤 key == 'xxx',如只查指定用户的,注意
            // 如果是整数查询,底层是使用Integer,如果是比较长的id会解析报错,建议都使用string
            queryBuilder.filterExpression("userId == '" + userId + "'");
        }
        SearchRequest searchRequest = queryBuilder
                // 只要最相似的 3 条,粗排一般捞出的文档比较多
                .topK(100)
                // 相似度评分≥0.1
                .similarityThreshold(0.1)
                .build();
        // 内部会调用org.springframework.ai.embedding.EmbeddingModel.embed()方法向量化查询关键字
        List<Document> hits = vectorStore.similaritySearch(searchRequest);
        return hits;
    }

    /**
     * 删除文档
     * @param docIds 是{@link Document#getId()}的集合
     */
    public void deleteByDocIds(List<String> docIds) {
        vectorStore.delete(docIds);
    }
}
