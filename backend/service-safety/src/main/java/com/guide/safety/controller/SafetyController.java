package com.guide.safety.controller;

import com.guide.common.result.R;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/tourist")
public class SafetyController {

    @PostMapping("/safety/fall-check")
    public R<String> fallCheck(@RequestParam Long userId, @RequestParam boolean cancelled) {
        return R.ok(cancelled ? "已取消" : "已触发应急响应");
    }

    @PostMapping("/safety/emergency-call")
    public R<String> emergencyCall(@RequestParam Long userId,
                                    @RequestParam Double lat, @RequestParam Double lng) {
        // TODO: 推送管理端告警 + 短信通知 + 拨打紧急联系人
        return R.ok("已发送求助信号");
    }
}
