package com.guide.rag.controller;

import com.guide.common.result.R;
import com.guide.rag.service.RagRetrievalService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * C端 RAG检索接口
 */
@RestController
@RequestMapping("/api/v1/tourist")
@RequiredArgsConstructor
public class RagQueryController {

    private final RagRetrievalService ragRetrievalService;

    @PostMapping("/rag/retrieve")
    public R<List<Map<String, Object>>> retrieve(@RequestParam String query,
                                                  @RequestParam(defaultValue = "5") int topK) {
        List<Map<String, Object>> chunks = ragRetrievalService.retrieve(query, topK).block();
        return R.ok(chunks);
    }
}
