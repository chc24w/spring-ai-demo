package com.chc.ai;

import com.alibaba.cloud.ai.advisor.RetrievalRerankAdvisor;
import com.alibaba.cloud.ai.dashscope.rerank.DashScopeRerankModel;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.postretrieval.document.DocumentPostProcessor;
import org.springframework.ai.rag.preretrieval.query.transformation.QueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.TranslationQueryTransformer;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RagTest {
    @Resource
    private ChatClient chatClient;
    @Resource
    private VectorStore vectorStore;
    @Resource
    private ChatClient.Builder chatClientBuilder;

    /**
     * 从vectorStore中查询文档后(粗排)再交给重排序模型进行精排
     * {@link RetrievalRerankAdvisor}:相当于{@link QuestionAnswerAdvisor} + 重排序
     * 测试重排序
     */
    @Test
    public void testRagReRankAdvisor(@Autowired DashScopeRerankModel dashScopeRerankModel) {
        String input = "什么是 VectorStore";
        input = "支持退货吗";
        RetrievalRerankAdvisor retrievalRerankAdvisor = new RetrievalRerankAdvisor(
                // 去向量db中查找
                vectorStore
                // 指定精排模型,精排默认使用前topN=5
                , dashScopeRerankModel
                // 粗排查出多少条
                , SearchRequest.builder().topK(200).build());
        String content = chatClient.prompt()
                .user(input)
                .advisors(retrievalRerankAdvisor)
                .call()
                .content();
        // content = 抱歉，提供的上下文中没有包含关于“VectorStore”的相关信息，因此我无法回答您的问题。
        System.out.println("content = " + content);
    }

    /**
     * 通过{@link RetrievalAugmentationAdvisor}实现rag,这个类包含
     * {@link QuestionAnswerAdvisor}的全部功能,比其更强大
     */
    @Test
    public void testRagViaRetrievalAugmentationAdvisor() {
        String input = "什么是 VectorStore";
        input = "支持退货吗";
        RetrievalAugmentationAdvisor retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor
                .builder()
                // 去向量db中按条件进行检索后返回,相当于QuestionAnswerAdvisor
                .documentRetriever(buildDocRetriever())
                // 查询前增强
                .queryAugmenter(buildQueryAugmenter())
                // 查询前的query转换器
                .queryTransformers(buildQueryTransformers())
                // 文档查询出来后的后置处理
                .documentPostProcessors(buildDocPostProcessors())
                .build();
        String content = chatClient.prompt()
                .user(input)
                .advisors(retrievalAugmentationAdvisor)
                .call()
                .content();
        System.out.println("content = " + content);
    }

    /**
     * 查询向量db前的处理
     *
     * @return 转换后的查询词, 用来去向量db中查询
     */
    private List<QueryTransformer> buildQueryTransformers() {
        List<QueryTransformer> list = new ArrayList<>();
        // 调用大模型优化查询词,去掉无关紧要的情绪词等,输出简洁的查询词
        RewriteQueryTransformer rewriteQueryTransformer = RewriteQueryTransformer.builder()
                .chatClientBuilder(chatClientBuilder)
                .targetSearchSystem("自定义知识库智能问答系统")
                .build();
        // 翻译转换器,如向量db只支持中文,但用户输入的是英文,不处理的话可能匹配度低,
        // 为了更好的进行匹配,则需要把用户输入的提示词转为中文
        TranslationQueryTransformer translationQueryTransformer = TranslationQueryTransformer
                .builder()
                .chatClientBuilder(chatClientBuilder)
                .targetLanguage("中文")
                .build();
        list.add(rewriteQueryTransformer);
        list.add(translationQueryTransformer);
        return list;
    }

    /**
     * 从向量db中出现出来后的处理
     */
    private List<DocumentPostProcessor> buildDocPostProcessors() {
        List<DocumentPostProcessor> list = new ArrayList<>();
        DocumentPostProcessor documentPostProcessor = new DocumentPostProcessor() {
            @Override
            public List<Document> process(Query query, List<Document> documents) {
                System.out.println("原始查询:" + query.text());
                System.out.println("查到的文档数量:" + documents.size());
                return documents;
            }
        };
        list.add(documentPostProcessor);
        return list;
    }


    private static ContextualQueryAugmenter buildQueryAugmenter() {
        return ContextualQueryAugmenter.builder()
                // false:当使用user input在向量db中查询不到结果时,直接返回不能回答
                // true:查询不到结果依然去问大模型
                .allowEmptyContext(false)
                // 默认是英文的:ContextualQueryAugmenter.DEFAULT_EMPTY_CONTEXT_PROMPT_TEMPLATE
                .emptyContextPromptTemplate(
                        PromptTemplate.builder()
                                .template("用户查询超出你的知识库范围,礼貌地告知用户你无法回答。")
                                .build()
                )
                .build();
    }

    private VectorStoreDocumentRetriever buildDocRetriever() {
        return VectorStoreDocumentRetriever.builder()
                .topK(5)
                .similarityThreshold(0.1)
                .vectorStore(vectorStore)
                .build();
    }

    /**
     * 通过{@link QuestionAnswerAdvisor}实现rag
     * 会使用user prompt去给定的vector store中查询,并增加到上下文中
     */
    @Test
    public void testRagViaQuestionAnswerAdvisor() {
//        String input = "什么是 VectorStore";
        String input = "支持退货吗";
        SearchRequest searchRequest = SearchRequest.builder()
                // 不需要自己设置query,advisor中会设置
//                .query(input)
                .topK(5)
                .similarityThreshold(0.5)
                .build();
        String content = chatClient.prompt()
                .user(input)
                .advisors(
                        // 自动去向量db中按条件进行检索后返回
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(searchRequest)
                                .build()
                )
                .call()
                .content();
        System.out.println("content = " + content);
    }
}
