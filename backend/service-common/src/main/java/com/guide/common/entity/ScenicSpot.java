package com.guide.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 景点围栏 (admin schema 可读写, tourist schema 只读)
 */
@Data
@TableName(value = "admin.scenic_spot", autoResultMap = true)
public class ScenicSpot {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long scenicId;
    private String spotName;
    private Double latitude;
    private Double longitude;
    private Integer radius;
    private Integer sortOrder;
    private String description;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
