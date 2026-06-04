package com.guide.rag.service;

import com.guide.rag.embedding.EmbeddingService;
import com.guide.rag.reranker.CrossEncoderService;
import com.guide.rag.vectorstore.MilvusClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RAG检索服务 — 多路召回 + 重排序
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RagRetrievalService {

    private final EmbeddingService embeddingService;
    private final MilvusClientService milvusClientService;
    private final CrossEncoderService crossEncoderService;

    /**
     * 检索相关知识块
     * @param query 查询文本
     * @param topK 召回数量
     * @return 重排序后的知识块列表
     */
    public Mono<List<Map<String, Object>>> retrieve(String query, int topK) {
        return embeddingService.embed(query)
                .map(queryVector -> {
                    // 多路召回（扩大召回量）
                    List<Map<String, Object>> candidates = milvusClientService.search(queryVector, topK * 2);

                    // 重排序
                    List<Map<String, Object>> reranked = crossEncoderService.rerank(query, candidates);

                    // 截取topK
                    return reranked.stream().limit(topK).collect(Collectors.toList());
                });
    }

    /**
     * 将检索结果格式化为Prompt上下文
     */
    public String formatContext(List<Map<String, Object>> chunks) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < chunks.size(); i++) {
            sb.append("【参考资料").append(i + 1).append("】\n");
            sb.append(chunks.get(i).get("chunk_text")).append("\n\n");
        }
        return sb.toString();
    }
}
