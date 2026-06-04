package com.guide.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 管理员用户 (admin schema)
 */
@Data
@TableName(value = "admin.admin_user", autoResultMap = true)
public class AdminUser {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String passwordHash;
    private String role;        // SUPER_ADMIN / SCENIC_ADMIN / EDITOR
    private Long scenicId;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
