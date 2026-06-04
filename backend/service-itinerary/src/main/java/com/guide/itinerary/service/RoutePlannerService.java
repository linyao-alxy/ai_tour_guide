package com.guide.itinerary.service;

import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class RoutePlannerService {
    public List<Map<String, Object>> recommend(Long scenicId, String profileJson) {
        // TODO: 实现基于用户画像的Dijkstra路线规划
        return List.of(Map.of("route", "推荐路线(待实现)", "scenicId", scenicId));
    }
}
