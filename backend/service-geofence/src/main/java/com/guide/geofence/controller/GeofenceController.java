package com.guide.geofence.controller;

import com.guide.common.dto.PositionReportDTO;
import com.guide.common.entity.ScenicSpot;
import com.guide.common.result.R;
import com.guide.geofence.service.GeofenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * C端 GPS位置上报 + 围栏判断
 */
@RestController
@RequestMapping("/api/v1/tourist")
@RequiredArgsConstructor
public class GeofenceController {

    private final GeofenceService geofenceService;

    @PostMapping("/position/report")
    public R<ScenicSpot> reportPosition(@RequestBody PositionReportDTO dto) {
        ScenicSpot spot = geofenceService.checkGeofence(
                dto.getLatitude(), dto.getLongitude(), dto.getScenicId());
        return R.ok(spot);
    }
}
