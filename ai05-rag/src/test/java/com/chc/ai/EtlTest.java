package com.chc.ai;

import com.chc.ai.service.DocTransformerService;
import com.chc.ai.service.DocumentReaderService;
import com.chc.ai.service.DocumentWriterService;
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
public class EtlTest {
    @jakarta.annotation.Resource
    private DocumentReaderService documentReaderService;
    @jakarta.annotation.Resource
    private DocTransformerService docTransformerService;
    @jakarta.annotation.Resource
    private DocumentWriterService documentWriterService;
    /**
     * 测试从向量db中删除
     */
    @Test
    public void testDelete() {
        documentWriterService.deleteByDocIds(List.of("3a9f23c1-6092-4a4e-bfb4-5873f0d54d7b"));
    }
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
    }
    /**
     * 测试写入到向量db
     */
    @Test
    public void testAdd(@Value("classpath:doc/电商售前客服一问一答高频场景速查表.md") Resource resource) {
        List<Document> documents = documentReaderService.read(resource);
        documents = docTransformerService.splitDocs(documents);
        for (Document document : documents) {
            // 模拟加入元数据用于测试过滤
            document.getMetadata().put("userId","999");
        }
        documentWriterService.add(documents);
        for (Document document : documents) {
            // 文档id:c0c48083-2986-4818-81df-21421c878795
            System.out.println("文档id:" + document.getId() + ",元数据:" + document.getMetadata());
        }
    }

    /**
     * 测试tika:支持的类型如PDF, DOC/DOCX, PPT/PPTX, XLS/XLSX, HTML
     */
    @Test
    public void testReadExcel(@Value("classpath:doc/test.xlsx") Resource resource) {
        documentReaderService.read(resource);
    }
    /**
     * 测试读取html文件
     */
    @Test
    public void testReadHtml(@Value("classpath:doc/test.html") Resource resource) {
        documentReaderService.readHtml(resource);
    }
    /**
     * 测试读取json文件
     */
    @Test
    public void testReadJson(@Value("classpath:doc/test.json") Resource resource) {
        documentReaderService.readJson(resource);
    }
    /**
     * 测试读取pdf文件
     */
    @Test
    public void testReadPdf(@Value("classpath:doc/1.pdf") Resource resource) {
        documentReaderService.readPdf(resource);
    }

    /**
     * 测试读取md文件
     */
    @Test
    public void testReadMd(@Value("classpath:doc/电商售前客服一问一答高频场景速查表.md") Resource resource) {
        documentReaderService.readMd(resource);
    }

    /**
     * 测试读取txt文件
     */
    @Test
    public void testReadText(@Value("classpath:doc/1.txt") Resource resource) {
        List<Document> documents = documentReaderService.readTxt(resource);
        documents = docTransformerService.splitDocs(documents);//拆分
        documents = docTransformerService.fillKeyword(documents);//填充关键词
        documents = docTransformerService.fillSummary(documents);//填充摘要
        System.out.println("documents.size() = " + documents.size());
    }
}
