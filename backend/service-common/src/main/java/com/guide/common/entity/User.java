package com.guide.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 游客用户
 */
@Data
@TableName(value = "tourist.user", autoResultMap = true)
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String openid;
    private String phone;
    private String nickname;
    private String avatarUrl;
    /** 用户画像标签 JSON: {"type":"family","pace":"slow","interests":["history"]} */
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Object profileTags;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
