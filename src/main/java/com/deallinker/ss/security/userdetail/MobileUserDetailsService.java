package com.deallinker.ss.security.userdetail;

import com.deallinker.ss.base.entity.SysUser;
import com.deallinker.ss.base.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description: TODO
 * @Author: tangsw
 * @Date 2020/12/1 18:28
 **/
@Slf4j
@Component
public class MobileUserDetailsService extends AbstractUserDetailsService {
    @Autowired
    SysUserService sysUserService;

    @Override
    public SysUser findSysUser(String usernameOrMobile) {
        log.info("请求的手机号是：" + usernameOrMobile);
        // 1. 通过手机号查询用户信息
        return sysUserService.findByMobile(usernameOrMobile);
    }
}

