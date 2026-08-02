package com.chc.ai.service;

import jakarta.annotation.Resource;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author zhaodaowen
 * @see <a href="https://www.roadjava.com">乐之者java</a>
 */
@Service
public class DocTransformerService {
    @Resource
    private ChatModel dashScopeChatModel;
    /**
     * 文档的拆分,不拆也行,如果拆分前文档有metadata,拆分后的每个文档依然会保留,即metadata不会丢失
     */
    public List<Document> splitDocs(List<Document> documents) {
        // 拆分:读出的文档需要拆分为一段/一片的chunk,这样更适合语义化
        // chunkSize:每个chunk的token数,默认800
        // minChunkSizeChars:每个chunk的最小字符数,>该值时找最后一个表示结束的标点符号(.!?\n),默认350
        // minChunkLengthToEmbed:丢弃少于此长度的chunk块,默认5
        // maxNumChunks:最多分为多少块,超过了则忽略,默认10000
        // keepSeparator:是否在块中保留分隔符、换行符,默认true
        TokenTextSplitter tokenTextSplitter = new TokenTextSplitter(800,
                350, 5, 10000, true);
        // 把分割好后的结果存入到向量数据库中即可
        List<Document> splitedList = tokenTextSplitter.split(documents);
        System.out.println("----拆分后大小----" + splitedList.size());
        return splitedList;
    }

    /**
     * 为每个文档提取关键字
     */
    public List<Document> fillKeyword(List<Document> docs) {
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(dashScopeChatModel,5);
        // 为每个文档的metadata填充名为excerpt_keywords的key表示提取出的关键字
        docs = enricher.apply(docs);
        return docs;
    }

    /**
     * 为每个文档提取摘要,包括当前文档的摘要以及与它相邻(前一个和后一个)的文档的摘要
     */
    public List<Document> fillSummary(List<Document> docs) {
        System.out.println("------执行摘要提取-------");
        SummaryMetadataEnricher enricher = new SummaryMetadataEnricher(dashScopeChatModel,
                List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS
                        , SummaryMetadataEnricher.SummaryType.CURRENT
                        , SummaryMetadataEnricher.SummaryType.NEXT));
        // 为每个文档的metadata填充名为:
        // prev_section_summary:上篇文档的摘要
        // section_summary:当前文档的摘要
        // next_section_summary:下篇文档的摘要
        docs = enricher.apply(docs);
        return docs;
    }
}
