package com.guide.aiproxy.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

/**
 * DeepSeek LLM 代理服务 — SSE流式调用
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LlmProxyService {

    private final WebClient webClient = WebClient.create("https://api.deepseek.com");

    @Value("${deepseek.api-key}")
    private String apiKey;

    private static final String SYSTEM_PROMPT = """
        你是一个智慧景区的AI数字人导游。请根据提供的参考资料回答游客的问题。
        要求:
        1. 回答准确、简洁、亲切
        2. 优先使用参考资料中的信息
        3. 如果没有相关参考资料，请如实告知
        4. 结合天气信息给出游览建议
        5. 回答末尾可以推荐1个相关的趣味知识
        """;

    public Flux<String> chatStream(String query, String context, String weatherInfo, String userProfile) {
        StringBuilder prompt = new StringBuilder();
        prompt.append(SYSTEM_PROMPT).append("\n\n");
        if (context != null && !context.isEmpty()) {
            prompt.append("【参考资料】\n").append(context).append("\n\n");
        }
        if (weatherInfo != null && !weatherInfo.isEmpty()) {
            prompt.append("【当前天气】\n").append(weatherInfo).append("\n\n");
        }
        if (userProfile != null && !userProfile.isEmpty()) {
            prompt.append("【游客偏好】\n").append(userProfile).append("\n\n");
        }
        prompt.append("【游客提问】\n").append(query);

        return webClient.post()
                .uri("/v1/chat/completions")
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .bodyValue(java.util.Map.of(
                        "model", "deepseek-chat",
                        "messages", java.util.List.of(
                                java.util.Map.of("role", "user", "content", prompt.toString())
                        ),
                        "stream", true,
                        "temperature", 0.7,
                        "max_tokens", 2048
                ))
                .retrieve()
                .bodyToFlux(String.class)
                .doOnNext(chunk -> log.debug("LLM chunk: {}", chunk))
                .doOnError(e -> log.error("LLM调用失败", e));
    }
}
