package com.guide.common.dto;

import lombok.Data;

/**
 * GPS位置上报
 */
@Data
public class PositionReportDTO {
    private Long userId;
    private Long scenicId;
    private Double latitude;
    private Double longitude;
}
