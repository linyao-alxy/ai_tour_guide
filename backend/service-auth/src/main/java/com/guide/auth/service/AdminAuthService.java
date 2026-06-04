package com.guide.auth.service;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guide.auth.config.JwtTokenProvider;
import com.guide.common.entity.AdminUser;
import com.guide.common.exception.BusinessException;
import com.guide.common.result.ResultCode;
import com.guide.auth.mapper.AdminUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * B端管理员鉴权服务
 */
@Service
@RequiredArgsConstructor
public class AdminAuthService {

    private final AdminUserMapper adminUserMapper;
    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, Object> login(String username, String password) {
        AdminUser admin = adminUserMapper.selectOne(
                new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, username));

        if (admin == null || !BCrypt.checkpw(password, admin.getPasswordHash())) {
            throw new BusinessException(ResultCode.PASSWORD_ERROR);
        }

        Map<String, Object> claims = new HashMap<>();
        claims.put("adminId", admin.getId());
        claims.put("role", admin.getRole());
        claims.put("scenicId", admin.getScenicId());

        String token = jwtTokenProvider.generateAdminToken(admin.getId(), claims);

        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("adminId", admin.getId());
        result.put("username", admin.getUsername());
        result.put("role", admin.getRole());
        result.put("scenicId", admin.getScenicId());
        return result;
    }

    public Map<String, Object> verifyToken(String token) {
        var claims = jwtTokenProvider.validateAdminToken(token);
        Map<String, Object> result = new HashMap<>();
        result.put("adminId", claims.getSubject());
        result.put("role", claims.get("role"));
        result.put("scenicId", claims.get("scenicId"));
        return result;
    }
}
