package com.guide.analytics.controller;

import com.guide.common.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/admin")
public class AnalyticsController {

    @GetMapping("/analytics/realtime")
    public R<Map<String, Object>> realtime(@RequestParam Long scenicId) {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("activeUsers", 128);
        data.put("todaySessions", 456);
        data.put("avgLatencyMs", 980);
        data.put("hotWords", List.of("灵山大佛","门票","洗手间"));
        return R.ok(data);
    }

    @GetMapping("/analytics/sentiment-report")
    public R<Map<String, Object>> sentimentReport(@RequestParam Long scenicId) {
        return R.ok(Map.of("positiveRate", "92%", "topComplaints", List.of("排队时间长","信号不好")));
    }
}
