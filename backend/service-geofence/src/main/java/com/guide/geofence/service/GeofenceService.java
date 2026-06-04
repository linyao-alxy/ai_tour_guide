package com.guide.geofence.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guide.common.entity.ScenicSpot;
import com.guide.geofence.mapper.ScenicSpotMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * 地理围栏判断服务 — Haversine公式计算GPS距离
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeofenceService {

    private final ScenicSpotMapper spotMapper;
    private final RedissonClient redissonClient;

    private static final double EARTH_RADIUS = 6371000.0; // 米

    /**
     * 判断当前GPS坐标是否进入了某个景点围栏
     */
    public ScenicSpot checkGeofence(double lat, double lng, Long scenicId) {
        // 从缓存加载该景区所有围栏
        List<ScenicSpot> spots = getSpots(scenicId);

        for (ScenicSpot spot : spots) {
            double distance = haversine(lat, lng, spot.getLatitude(), spot.getLongitude());
            if (distance <= spot.getRadius()) {
                log.debug("进入围栏: spot={}, distance={}m", spot.getSpotName(), String.format("%.1f", distance));
                return spot;
            }
        }
        return null; // 未进入任何围栏
    }

    @SuppressWarnings("unchecked")
    private List<ScenicSpot> getSpots(Long scenicId) {
        String cacheKey = "geofence:" + scenicId;
        RBucket<List<ScenicSpot>> bucket = redissonClient.getBucket(cacheKey);
        List<ScenicSpot> cached = bucket.get();
        if (cached != null) return cached;

        List<ScenicSpot> spots = spotMapper.selectList(
                new LambdaQueryWrapper<ScenicSpot>()
                        .eq(ScenicSpot::getScenicId, scenicId)
                        .orderByAsc(ScenicSpot::getSortOrder));
        bucket.set(spots, Duration.ofHours(1));
        return spots;
    }

    /**
     * Haversine公式 — 计算两点间距离(米)
     */
    public static double haversine(double lat1, double lng1, double lat2, double lng2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS * c;
    }
}
