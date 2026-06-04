package com.guide.common.dto;

import lombok.Data;

/**
 * 用户画像
 */
@Data
public class UserProfileDTO {
    private String type;            // family/couple/solo/elder
    private String pace;            // slow/normal/fast
    private String[] interests;     // ["history","nature","photo","food"]
    private Boolean accessibility;  // 无障碍需求
}
