package com.guide.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guide.common.entity.AdminUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AdminUserMapper extends BaseMapper<AdminUser> {
}
