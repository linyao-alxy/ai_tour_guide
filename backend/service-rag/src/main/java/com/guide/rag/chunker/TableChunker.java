package com.guide.rag.chunker;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 表格行级分块器 — 按 ### 标题分割
 */
@Component
public class TableChunker implements ChunkStrategy {

    @Override
    public ChunkResult chunk(String text) {
        List<String> chunks = new ArrayList<>();
        String[] sections = text.split("\n(?=### )");
        for (String section : sections) {
            String trimmed = section.trim();
            if (!trimmed.isEmpty()) {
                chunks.add(trimmed);
            }
        }
        return new ChunkResult(chunks, "TABLE");
    }
}
