package com.guide.common.result;

import lombok.Getter;

/**
 * 业务错误码枚举
 */
@Getter
public enum ResultCode {
    SUCCESS(200, "操作成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "无权限"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    SERVICE_UNAVAILABLE(503, "服务暂不可用"),

    // 业务错误码 (1xxxx)
    USER_NOT_FOUND(10001, "用户不存在"),
    PASSWORD_ERROR(10002, "密码错误"),
    TOKEN_EXPIRED(10003, "令牌已过期"),
    SCENIC_NOT_FOUND(10004, "景区不存在"),
    KNOWLEDGE_NOT_FOUND(10005, "知识条目不存在"),
    AI_SERVICE_ERROR(10006, "AI服务调用失败"),
    ;

    private final Integer code;
    private final String message;

    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
