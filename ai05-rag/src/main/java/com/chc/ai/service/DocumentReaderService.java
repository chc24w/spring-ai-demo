package com.chc.ai.service;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Service
public class DocumentReaderService {

    /**
     * 测试tika:支持的类型如PDF, DOC/DOCX, PPT/PPTX, XLS/XLSX, HTML
     */
    public List<Document> read(Resource resource) {
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(resource);
        List<Document> documents = tikaDocumentReader.read();
        printContent(documents);
        return documents;
    }
    /**
     * 测试读取html文件
     */
    public void readHtml(Resource resource) {
        JsoupDocumentReaderConfig config = JsoupDocumentReaderConfig.builder()
                .selector("article p") // Extract paragraphs within <article> tags
                .charset("utf-8")  // Use ISO-8859-1 encoding
                .includeLinkUrls(true) // 包含文档中的href值 in metadata
                // metadata={date=2024-01-15, linkUrls=[, , https://www.example.com], source=my-page.html, title=My Web Page, author=John Doe}
                .metadataTags(List.of("author", "date")) // 抽取meta标签
                .additionalMetadata("source", "my-page.html") // Add custom metadata
                .build();

        JsoupDocumentReader reader = new JsoupDocumentReader(resource, config);
        List<Document> documents = reader.get();
        printContent(documents);
    }
    /**
     * 测试读取json文件
     */
    public void readJson(Resource resource) {
        // 只读取指定的key,对源文件是json数组还是json对象没有要求
        JsonReader jsonReader = new JsonReader(resource, "description", "content");
        List<Document> documents = jsonReader.get();
        printContent(documents);
    }
    /**
     * 测试读取pdf文件
     */
    public void readPdf(Resource resource) {
        // 一页读为一个文档
        PagePdfDocumentReader reader = new PagePdfDocumentReader(resource,
                PdfDocumentReaderConfig.builder().build());
        List<Document> documents = reader.read();
        printContent(documents);
    }

    /**
     * 测试读取md文件
     */
    public void readMd(Resource resource) {
        MarkdownDocumentReaderConfig readerConfig = MarkdownDocumentReaderConfig.builder()
                // 水平分割线是否创建新文档 默认false:不创建
                .withHorizontalRuleCreateDocument(false)
                // 文档是否包含引用  默认false:不包含,表示引用单独创建为文档
                .withIncludeBlockquote(false)
                // 文档是否包含代码 默认false:不包含,表示代码单独创建为文档
                .withIncludeCodeBlock(false)
                .withAdditionalMetadata("filename", resource.getFilename())
                .build();
        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, readerConfig);
        List<Document> documents = reader.read();
        printContent(documents);
    }

    /**
     * 测试读取txt文件
     */
    public List<Document> readTxt(Resource resource) {
        TextReader textReader = new TextReader(resource);
        textReader.getCustomMetadata().put("filename",resource.getFilename());
        List<Document> documents = textReader.read();
        printContent(documents);
        return documents;
    }

    /**
     * 文件日志打印
     * @param documents
     */
    private void printContent(List<Document> documents) {
        System.out.println("------读取到"+documents.size()+"个文档----");
        for (Document document : documents) {
            System.out.println("文档内容:" + document.getText());
        }
    }
}
