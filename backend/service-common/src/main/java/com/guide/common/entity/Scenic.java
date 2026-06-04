package com.guide.common.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 景区信息 (admin schema)
 */
@Data
@TableName(value = "admin.scenic", autoResultMap = true)
public class Scenic {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String scenicName;
    private String province;
    private String city;
    private Double latitude;
    private Double longitude;
    private String address;
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;
}
