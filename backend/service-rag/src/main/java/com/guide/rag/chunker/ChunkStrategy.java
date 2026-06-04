package com.guide.rag.chunker;

import java.util.List;

/**
 * 分块策略接口
 */
public interface ChunkStrategy {
    ChunkResult chunk(String text);
}
