package com.guide.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 对话会话
 */
@Data
@TableName(value = "tourist.chat_session", autoResultMap = true)
public class ChatSession {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long scenicId;
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object positionLog;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
}
