package com.guide.auth.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guide.auth.config.JwtTokenProvider;
import com.guide.common.entity.User;
import com.guide.common.exception.BusinessException;
import com.guide.common.result.ResultCode;
import com.guide.auth.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * C端游客鉴权服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TouristAuthService {

    private final UserMapper userMapper;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 微信OAuth登录
     */
    public Map<String, Object> wechatLogin(String code) {
        // TODO: 调用微信API用code换取openid
        String openid = "mock_openid_" + code;

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getOpenid, openid));

        if (user == null) {
            user = new User();
            user.setOpenid(openid);
            user.setNickname("游客" + System.currentTimeMillis() % 10000);
            userMapper.insert(user);
        }

        return buildTokenResponse(user, "wechat");
    }

    /**
     * 手机验证码登录
     */
    public Map<String, Object> phoneLogin(String phone, String verifyCode) {
        // TODO: 验证短信验证码
        if (!"000000".equals(verifyCode)) {
            throw new BusinessException(ResultCode.BAD_REQUEST.getCode(), "验证码错误");
        }

        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>().eq(User::getPhone, phone));

        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user.setNickname("游客" + phone.substring(phone.length() - 4));
            userMapper.insert(user);
        }

        return buildTokenResponse(user, "phone");
    }

    /**
     * 验证令牌
     */
    public Map<String, Object> verifyToken(String token) {
        var claims = jwtTokenProvider.validateTouristToken(token);
        String userId = claims.getSubject();

        User user = userMapper.selectById(Long.valueOf(userId));
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userId", user.getId());
        result.put("nickname", user.getNickname());
        return result;
    }

    private Map<String, Object> buildTokenResponse(User user, String loginType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", user.getId());
        claims.put("loginType", loginType);

        String token = jwtTokenProvider.generateTouristToken(user.getId(), claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("nickname", user.getNickname());
        return result;
    }
}
