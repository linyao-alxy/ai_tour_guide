package com.guide.rag.controller;

import com.guide.common.entity.Document;
import com.guide.common.entity.KnowledgeChunk;
import com.guide.common.result.R;
import com.guide.rag.service.KnowledgeIngestService;
import com.guide.rag.mapper.DocumentMapper;
import com.guide.rag.mapper.KnowledgeChunkMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

/**
 * B端 知识库管理接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class KnowledgeAdminController {

    private final MinioClient minioClient;
    private final DocumentMapper documentMapper;
    private final KnowledgeChunkMapper chunkMapper;
    private final KnowledgeIngestService ingestService;

    @Value("${minio.bucket:documents}")
    private String bucket;

    /**
     * 上传文档
     */
    @PostMapping("/knowledge/upload")
    public R<String> upload(@RequestParam("file") MultipartFile file,
                             @RequestParam Long scenicId,
                             @RequestParam Long uploadedBy) throws Exception {
        String fileName = file.getOriginalFilename();
        String fileType = getFileType(fileName);
        String objectPath = "scenic_" + scenicId + "/" + UUID.randomUUID() + "_" + fileName;

        // 上传到MinIO
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucket).object(objectPath)
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(file.getContentType()).build());

        // 保存文档记录
        Document doc = new Document();
        doc.setScenicId(scenicId);
        doc.setFileName(fileName);
        doc.setFileType(fileType);
        doc.setFileSize(file.getSize());
        doc.setMinioPath(objectPath);
        doc.setStatus("UPLOADED");
        doc.setUploadedBy(uploadedBy);
        documentMapper.insert(doc);

        // 异步处理文档
        new Thread(() -> ingestService.processDocument(doc.getId())).start();

        return R.ok("uploaded, docId=" + doc.getId());
    }

    /**
     * 知识条目列表
     */
    @GetMapping("/knowledge/list")
    public R<List<KnowledgeChunk>> list(@RequestParam Long scenicId) {
        List<KnowledgeChunk> list = chunkMapper.selectList(
                new LambdaQueryWrapper<KnowledgeChunk>().eq(KnowledgeChunk::getScenicId, scenicId));
        return R.ok(list);
    }

    /**
     * 删除知识条目
     */
    @DeleteMapping("/knowledge/{id}")
    public R<Void> delete(@PathVariable Long id) {
        chunkMapper.deleteById(id);
        return R.ok();
    }

    private String getFileType(String fileName) {
        String upper = fileName.toUpperCase();
        if (upper.endsWith(".XLSX")) return "XLSX";
        if (upper.endsWith(".DOCX")) return "DOCX";
        if (upper.endsWith(".PDF")) return "PDF";
        if (upper.endsWith(".TXT")) return "TXT";
        throw new IllegalArgumentException("不支持的文件格式: " + fileName);
    }
}
