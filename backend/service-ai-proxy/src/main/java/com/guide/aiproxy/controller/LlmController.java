package com.guide.aiproxy.controller;

import com.guide.common.result.R;
import com.guide.aiproxy.service.LlmProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

/**
 * LLM代理 Controller — SSE流式输出
 */
@RestController
@RequestMapping("/api/v1/tourist")
@RequiredArgsConstructor
public class LlmController {

    private final LlmProxyService llmProxyService;

    @PostMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String query,
                                    @RequestParam String context,
                                    @RequestParam(required = false) String weatherInfo,
                                    @RequestParam(required = false) String userProfile) {
        return llmProxyService.chatStream(query, context, weatherInfo, userProfile);
    }
}
