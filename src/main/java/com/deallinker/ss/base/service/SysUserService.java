package com.deallinker.ss.base.service;

import com.deallinker.ss.base.entity.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author tangsw
 * @since 2020-12-01
 */
public interface SysUserService extends IService<SysUser> {

    SysUser findByUsername(String usernameOrMobile);

    SysUser findByMobile(String usernameOrMobile);
}
