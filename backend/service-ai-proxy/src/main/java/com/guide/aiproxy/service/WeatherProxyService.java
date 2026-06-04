package com.guide.aiproxy.service;

import com.guide.common.entity.Scenic;
import com.guide.aiproxy.mapper.ScenicMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 和风天气代理服务 — 含Redis缓存
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherProxyService {

    private final WebClient webClient = WebClient.create("https://devapi.qweather.com");
    private final ScenicMapper scenicMapper;
    private final RedissonClient redissonClient;

    @Value("${qweather.api-key}")
    private String apiKey;

    public Map<String, Object> getCurrentWeather(Long scenicId) {
        String cacheKey = "weather:" + scenicId;
        RBucket<Map<String, Object>> bucket = redissonClient.getBucket(cacheKey);

        // 查缓存
        Map<String, Object> cached = bucket.get();
        if (cached != null) return cached;

        // 查询景区坐标
        Scenic scenic = scenicMapper.selectById(scenicId);
        String location = scenic.getLongitude() + "," + scenic.getLatitude();

        // 调用和风天气API
        Map<String, Object> weather = new LinkedHashMap<>();
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> nowResp = webClient.get()
                    .uri("/v7/weather/now?location={loc}&key={key}", location, apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();

            var now = (Map<String, Object>) nowResp.get("now");
            weather.put("temp", now.get("temp"));
            weather.put("feelsLike", now.get("feelsLike"));
            weather.put("condition", now.get("text"));
            weather.put("windDir", now.get("windDir"));
            weather.put("windScale", now.get("windScale"));
            weather.put("humidity", now.get("humidity"));

            // 检查极端天气预警
            checkAlerts(location, weather);

            // 写入缓存 30分钟
            bucket.set(weather, Duration.ofMinutes(30));

        } catch (Exception e) {
            log.error("天气API调用失败", e);
            weather.put("temp", "--");
            weather.put("condition", "未知");
        }
        return weather;
    }

    @SuppressWarnings("unchecked")
    private void checkAlerts(String location, Map<String, Object> weather) {
        try {
            Map<String, Object> warnResp = webClient.get()
                    .uri("/v7/warning/now?location={loc}&key={key}", location, apiKey)
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            var warningList = (java.util.List<Map<String, Object>>) warnResp.get("warning");
            if (warningList != null && !warningList.isEmpty()) {
                var warn = warningList.get(0);
                weather.put("alert", Map.of(
                        "type", warn.get("typeName"),
                        "level", warn.get("level"),
                        "advice", warn.get("text")
                ));
            }
        } catch (Exception ignored) {}
    }
}
