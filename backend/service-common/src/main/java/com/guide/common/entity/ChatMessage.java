package com.guide.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 对话消息
 */
@Data
@TableName(value = "tourist.chat_message", autoResultMap = true)
public class ChatMessage {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long sessionId;
    private String role;        // USER / ASSISTANT
    private String content;
    private String audioUrl;
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object chunksUsed;
    private Integer latencyMs;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
