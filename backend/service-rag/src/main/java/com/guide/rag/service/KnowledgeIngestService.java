package com.guide.rag.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guide.common.entity.Document;
import com.guide.common.entity.KnowledgeChunk;
import com.guide.common.exception.BusinessException;
import com.guide.rag.chunker.ChunkResult;
import com.guide.rag.chunker.ChunkerFactory;
import com.guide.rag.embedding.EmbeddingService;
import com.guide.rag.mapper.DocumentMapper;
import com.guide.rag.mapper.KnowledgeChunkMapper;
import com.guide.rag.parser.DocumentParser;
import com.guide.rag.parser.ParserFactory;
import com.guide.rag.preprocessor.DocumentType;
import com.guide.rag.preprocessor.TypeRouter;
import com.guide.rag.vectorstore.MilvusClientService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * 知识库文档摄取服务 — 上传→解析→分块→嵌入→Milvus写入
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeIngestService {

    private final MinioClient minioClient;
    private final ParserFactory parserFactory;
    private final TypeRouter typeRouter;
    private final ChunkerFactory chunkerFactory;
    private final EmbeddingService embeddingService;
    private final MilvusClientService milvusClientService;
    private final DocumentMapper documentMapper;
    private final KnowledgeChunkMapper chunkMapper;

    @Value("${minio.bucket:documents}")
    private String bucket;

    /**
     * 完整处理管道: 下载MinIO文件 → 解析 → 分块 → 嵌入 → Milvus
     */
    public void processDocument(Long documentId) {
        // 1. 获取文档记录
        Document doc = documentMapper.selectById(documentId);
        if (doc == null) throw new BusinessException("文档不存在: " + documentId);

        try {
            // 更新状态：分块中
            doc.setStatus("CHUNKING");
            documentMapper.updateById(doc);

            // 2. 从MinIO下载文件
            byte[] fileBytes = downloadFromMinio(doc.getMinioPath());

            // 3. 解析文档
            DocumentParser parser = parserFactory.getParser(doc.getFileType());
            String text = parser.parse(fileBytes, doc.getFileName());

            // 4. 确定文档类型并分块
            DocumentType docType = typeRouter.route(doc.getFileType(), text);
            ChunkResult chunkResult = chunkerFactory.getChunker(docType).chunk(text);

            // 更新状态：向量化中
            doc.setChunkCount(chunkResult.chunks().size());
            doc.setStatus("VECTORIZING");
            documentMapper.updateById(doc);

            // 5. 嵌入 + 写入Milvus（批量处理）
            for (int i = 0; i < chunkResult.chunks().size(); i++) {
                String chunk = chunkResult.chunks().get(i);
                List<Float> embedding = embeddingService.embed(chunk).block();

                long milvusId = milvusClientService.insert(
                        embedding, chunk, documentId, doc.getScenicId(), chunkResult.chunkType());

                // 保存知识块元数据到PostgreSQL
                KnowledgeChunk kc = new KnowledgeChunk();
                kc.setDocumentId(documentId);
                kc.setScenicId(doc.getScenicId());
                kc.setChunkIndex(i);
                kc.setChunkText(chunk);
                kc.setChunkType(chunkResult.chunkType());
                kc.setMilvusId(milvusId);
                chunkMapper.insert(kc);
            }

            // 更新状态：完成
            doc.setStatus("DONE");
            documentMapper.updateById(doc);

            log.info("文档处理完成: docId={}, chunks={}", documentId, chunkResult.chunks().size());

        } catch (Exception e) {
            log.error("文档处理失败: docId={}", documentId, e);
            doc.setStatus("FAILED");
            documentMapper.updateById(doc);
        }
    }

    private byte[] downloadFromMinio(String objectPath) throws Exception {
        try (var stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucket).object(objectPath).build());
             var bos = new ByteArrayOutputStream()) {
            stream.transferTo(bos);
            return bos.toByteArray();
        }
    }
}
