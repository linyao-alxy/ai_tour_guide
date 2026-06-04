package com.guide.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 文档上传记录 (admin schema)
 */
@Data
@TableName(value = "admin.document", autoResultMap = true)
public class Document {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scenicId;
    private String fileName;
    private String fileType;    // XLSX/DOCX/PDF/TXT
    private Long fileSize;
    private String minioPath;
    private Integer chunkCount;
    private String status;      // UPLOADED/CHUNKING/VECTORIZING/DONE/FAILED
    private Long uploadedBy;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
