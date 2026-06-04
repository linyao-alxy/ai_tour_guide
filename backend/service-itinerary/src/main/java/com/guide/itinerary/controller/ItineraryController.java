package com.guide.itinerary.controller;

import com.guide.common.dto.UserProfileDTO;
import com.guide.common.result.R;
import com.guide.itinerary.service.RoutePlannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/tourist")
@RequiredArgsConstructor
public class ItineraryController {

    private final RoutePlannerService routePlanner;

    @GetMapping("/itinerary/recommend")
    public R<List<Map<String, Object>>> recommend(@RequestParam Long scenicId,
                                                   @RequestParam(required = false) String profileJson) {
        return R.ok(routePlanner.recommend(scenicId, profileJson));
    }
}
