package com.guide.auth.controller;

import com.guide.auth.service.TouristAuthService;
import com.guide.common.result.R;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * C端游客鉴权
 */
@RestController
@RequestMapping("/api/v1/tourist/auth")
@RequiredArgsConstructor
public class TouristAuthController {

    private final TouristAuthService touristAuthService;

    @PostMapping("/wechat-login")
    public R<Map<String, Object>> wechatLogin(@RequestParam String code) {
        Map<String, Object> result = touristAuthService.wechatLogin(code);
        return R.ok(result);
    }

    @PostMapping("/phone-login")
    public R<Map<String, Object>> phoneLogin(@RequestParam String phone,
                                              @RequestParam String verifyCode) {
        Map<String, Object> result = touristAuthService.phoneLogin(phone, verifyCode);
        return R.ok(result);
    }

    @GetMapping("/verify")
    public R<Map<String, Object>> verifyToken(@RequestHeader("Authorization") String token) {
        Map<String, Object> result = touristAuthService.verifyToken(token.replace("Bearer ", ""));
        return R.ok(result);
    }
}
