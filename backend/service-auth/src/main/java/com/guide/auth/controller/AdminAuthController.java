package com.guide.auth.controller;

import com.guide.auth.service.AdminAuthService;
import com.guide.common.result.R;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * B端管理员鉴权
 */
@RestController
@RequestMapping("/api/v1/admin/auth")
@RequiredArgsConstructor
public class AdminAuthController {

    private final AdminAuthService adminAuthService;

    @PostMapping("/login")
    public R<Map<String, Object>> login(@RequestParam String username,
                                         @RequestParam String password) {
        Map<String, Object> result = adminAuthService.login(username, password);
        return R.ok(result);
    }

    @GetMapping("/verify")
    public R<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = adminAuthService.verifyToken(token.replace("Bearer ", ""));
        return R.ok(result);
    }
}
