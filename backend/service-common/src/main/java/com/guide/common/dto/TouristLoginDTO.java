package com.guide.common.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 游客登录请求
 */
@Data
public class TouristLoginDTO {
    /** 微信code */
    private String code;
    /** 手机号 */
    private String phone;
    /** 验证码 */
    private String verifyCode;
    /** 登录类型: wechat/phone */
    @NotBlank(message = "登录类型不能为空")
    private String loginType;
}
