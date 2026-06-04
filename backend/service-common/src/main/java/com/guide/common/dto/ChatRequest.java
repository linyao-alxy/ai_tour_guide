package com.guide.common.dto;

import lombok.Data;

/**
 * 对话请求
 */
@Data
public class ChatRequest {
    private Long sessionId;
    /** 游客提问文本（语音已转文字时直接用此字段） */
    private String query;
    /** 当前GPS纬度 */
    private Double lat;
    /** 当前GPS经度 */
    private Double lng;
    /** 音色ID */
    private String voiceId;
    /** 景区ID */
    private Long scenicId;
}
