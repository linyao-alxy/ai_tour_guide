package com.guide.rag.chunker;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 语义段落分块器 — 按段落边界+滑动窗口
 */
@Component
public class SemanticChunker implements ChunkStrategy {

    private static final int CHUNK_SIZE = 512;   // 约512个字符
    private static final int OVERLAP = 64;

    @Override
    public ChunkResult chunk(String text) {
        List<String> chunks = new ArrayList<>();
        String[] paragraphs = text.split("\n\n+");

        StringBuilder current = new StringBuilder();
        for (String p : paragraphs) {
            String trimmed = p.trim();
            if (trimmed.isEmpty()) continue;

            if (current.length() + trimmed.length() > CHUNK_SIZE && current.length() > 0) {
                chunks.add(current.toString().trim());
                // 滑动窗口重叠
                if (current.length() > OVERLAP) {
                    current = new StringBuilder(current.substring(current.length() - OVERLAP));
                } else {
                    current = new StringBuilder();
                }
            }
            if (!current.isEmpty()) current.append("\n\n");
            current.append(trimmed);
        }
        if (!current.isEmpty()) {
            chunks.add(current.toString().trim());
        }
        return new ChunkResult(chunks, "NARRATIVE");
    }
}
