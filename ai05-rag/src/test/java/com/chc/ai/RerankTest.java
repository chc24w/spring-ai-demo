package com.chc.ai;

import com.chc.ai.service.DocumentWriterService;
import com.chc.ai.service.RerankModelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.document.Document;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RerankTest {
    @jakarta.annotation.Resource
    private RerankModelService rerankModelService;
    @jakarta.annotation.Resource
    private DocumentWriterService documentWriterService;

    /**
     * 测试从向量db查询
     */
    @Test
    public void testSearch() {
        List<Document> documents = documentWriterService.search("支持退货吗", 999L);
        for (Document document : documents) {
            System.out.println("文档id:" + document.getId() + ",评分:" + document.getScore()
                    + ",元数据:" + document.getMetadata());
        }
        System.out.println("---------重排后----------");
        List<Document> reranked = rerankModelService.rerank("支持退货吗", documents);
        for (Document document : reranked) {
            System.out.println("文档id:" + document.getId() + ",评分:" + document.getScore()
                    + ",元数据:" + document.getMetadata());
        }
    }
}
