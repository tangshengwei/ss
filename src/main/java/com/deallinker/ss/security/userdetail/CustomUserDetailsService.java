package com.deallinker.ss.security.userdetail;

import com.deallinker.ss.base.entity.SysUser;
import com.deallinker.ss.base.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * @Description: TODO
 * @Author: tangsw
 * @Date 2020/12/1 18:28
 **/
@Slf4j
@Component
public class CustomUserDetailsService extends AbstractUserDetailsService {

    @Autowired // 不能删掉，不然报错
    PasswordEncoder passwordEncoder;

    @Autowired
    SysUserService sysUserService;

    @Override
    public SysUser findSysUser(String usernameOrMobile) {
        log.info("请求认证的用户名: " + usernameOrMobile);
        // 1. 通过请求的用户名去数据库中查询用户信息
        return sysUserService.findByUsername(usernameOrMobile);
    }


}

