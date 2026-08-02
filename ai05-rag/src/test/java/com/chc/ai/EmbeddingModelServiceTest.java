package com.chc.ai;

import com.chc.ai.service.DocTransformerService;
import com.chc.ai.service.DocumentReaderService;
import com.chc.ai.service.EmbeddingModelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EmbeddingModelServiceTest {
    @jakarta.annotation.Resource
    private DocumentReaderService documentReaderService;
    @jakarta.annotation.Resource
    private DocTransformerService docTransformerService;
    @jakarta.annotation.Resource
    private EmbeddingModelService embeddingModelService;
    @Test
    public void testEmbeddingCall(@Value("classpath:doc/1.txt") Resource resource) {
        List<Document> documents = documentReaderService.readTxt(resource);
        documents = docTransformerService.splitDocs(documents);
        embeddingModelService.testEmbeddingCall(documents.get(0).getText());
    }

    @Test
    public void testEmbeddingEmbed(@Value("classpath:doc/1.txt") Resource resource) {
        List<Document> documents = documentReaderService.readTxt(resource);
        documents = docTransformerService.splitDocs(documents);
        embeddingModelService.testEmbeddingEmbed(documents.get(0));
    }
}
