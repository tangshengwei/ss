package com.deallinker.ss.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.deallinker.ss.base.entity.SysUser;
import com.deallinker.ss.base.mapper.SysUserMapper;
import com.deallinker.ss.base.service.SysUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author tangsw
 * @since 2020-12-01
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser findByUsername(String usernameOrMobile) {
        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.lambda().eq(SysUser::getUsername, usernameOrMobile);
        SysUser user = getOne(sysUserQueryWrapper);
        return user;
    }

    @Override
    public SysUser findByMobile(String usernameOrMobile) {
        QueryWrapper<SysUser> sysUserQueryWrapper = new QueryWrapper<>();
        sysUserQueryWrapper.lambda().eq(SysUser::getMobile, usernameOrMobile);
        SysUser user = getOne(sysUserQueryWrapper);
        return user;
    }
}
