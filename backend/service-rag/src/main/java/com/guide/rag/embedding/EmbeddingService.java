package com.guide.rag.embedding;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 阿里云 DashScope Embedding 服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmbeddingService {

    private final WebClient webClient = WebClient.create("https://dashscope.aliyuncs.com");

    @Value("${dashscope.api-key}")
    private String apiKey;

    /**
     * 单文本向量化
     */
    public Mono<List<Float>> embed(String text) {
        return webClient.post()
                .uri("/api/v1/services/embeddings/text-embedding/text-embedding")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of(
                        "model", "text-embedding-v3",
                        "input", Map.of("texts", List.of(text)),
                        "parameters", Map.of("text_type", "document")
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var output = (Map<String, Object>) response.get("output");
                    var embeddings = (List<Map<String, Object>>) output.get("embeddings");
                    var embedding = (List<Number>) embeddings.get(0).get("embedding");
                    return embedding.stream().map(Number::floatValue).toList();
                })
                .doOnError(e -> log.error("Embedding调用失败", e));
    }

    /**
     * 批量文本向量化
     */
    @SuppressWarnings("unchecked")
    public Mono<List<List<Float>>> embedBatch(List<String> texts) {
        return webClient.post()
                .uri("/api/v1/services/embeddings/text-embedding/text-embedding")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(Map.of(
                        "model", "text-embedding-v3",
                        "input", Map.of("texts", texts),
                        "parameters", Map.of("text_type", "document")
                ))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> {
                    var output = (Map<String, Object>) response.get("output");
                    var embeddings = (List<Map<String, Object>>) output.get("embeddings");
                    return embeddings.stream()
                            .map(e -> ((List<Number>) e.get("embedding")).stream()
                                    .map(Number::floatValue).toList())
                            .toList();
                });
    }
}
