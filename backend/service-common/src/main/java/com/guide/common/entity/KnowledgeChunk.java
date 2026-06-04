package com.guide.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 知识块元数据 (admin schema)
 */
@Data
@TableName(value = "admin.knowledge_chunk", autoResultMap = true)
public class KnowledgeChunk {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long documentId;
    private Long scenicId;
    private Integer chunkIndex;
    private String chunkText;
    private String chunkType;   // NARRATIVE/STRUCTURED/TABLE
    private Long milvusId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
