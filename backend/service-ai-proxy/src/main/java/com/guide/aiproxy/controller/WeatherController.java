package com.guide.aiproxy.controller;

import com.guide.common.result.R;
import com.guide.aiproxy.service.WeatherProxyService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 天气代理 Controller
 */
@RestController
@RequestMapping("/api/v1/tourist")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherProxyService weatherProxyService;

    @GetMapping("/weather/current")
    public R<Map<String, Object>> getCurrentWeather(@RequestParam Long scenicId) {
        Map<String, Object> weather = weatherProxyService.getCurrentWeather(scenicId);
        return R.ok(weather);
    }
}
