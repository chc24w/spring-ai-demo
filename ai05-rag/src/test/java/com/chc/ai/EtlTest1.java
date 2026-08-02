package com.chc.ai;

import com.chc.ai.service.DocTransformerService;
import com.chc.ai.service.DocumentReaderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class EtlTest1 {

    @jakarta.annotation.Resource
    private DocumentReaderService documentReaderService;
    @jakarta.annotation.Resource
    private DocTransformerService docTransformerService;

    /**
     * 测试读取txt文件
     */
    @Test
    public void testReadText(@Value("classpath:doc/1.txt") Resource resource) {
        List<Document> documents = documentReaderService.readTxt(resource);
    }

    /**
     * 测试读取md文件
     */
    @Test
    public void testReadMd(@Value("classpath:doc/电商售前客服一问一答高频场景速查表.md") Resource resource) {
        documentReaderService.readMd(resource);
    }

    /**
     * 测试读取pdf文件
     */
    @Test
    public void testReadPdf(@Value("classpath:doc/1.pdf") Resource resource) {
        documentReaderService.readPdf(resource);
    }

    /**
     * 测试读取Json文件
     */
    @Test
    public void testReadJson(@Value("classpath:doc/test.json") Resource resource) {
        documentReaderService.readJson(resource);
    }

    /**
     * 测试读取html文件
     */
    @Test
    public void testReadHtml(@Value("classpath:doc/test.html") Resource resource) {
        documentReaderService.readHtml(resource);
    }

    /**
     * 测试万能读取器(支持的类型：PDF,DOC/DOCX,PPT/PPTX,XLS/XLSX,HTML)
     */
    @Test
    public void testReadExcel(@Value("classpath:doc/test.xlsx") Resource resource) {
        documentReaderService.read(resource);
    }
}
