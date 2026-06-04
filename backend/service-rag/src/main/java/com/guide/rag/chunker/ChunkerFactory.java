package com.guide.rag.chunker;

import com.guide.rag.preprocessor.DocumentType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 分块器工厂
 */
@Component
@RequiredArgsConstructor
public class ChunkerFactory {

    private final SemanticChunker semanticChunker;
    private final TableChunker tableChunker;

    public ChunkStrategy getChunker(DocumentType type) {
        return switch (type) {
            case NARRATIVE -> semanticChunker;
            case TABLE, STRUCTURED -> tableChunker;
        };
    }
}
