package com.guide.rag.chunker;

import java.util.List;

/**
 * 文档分块记录
 */
public record ChunkResult(List<String> chunks, String chunkType) {
}
