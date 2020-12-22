package com.deallinker.ss.security.token;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.deallinker.ss.base.entity.SysPermission;
import com.deallinker.ss.base.entity.SysUser;
import com.deallinker.ss.base.service.SysPermissionService;
import com.deallinker.ss.base.service.SysUserService;
import com.deallinker.ss.security.userdetail.AbstractUserDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @Author: tangsw
 * @Date 2020/12/21 15:33
 **/
@Slf4j
@Service
public class TokenUserDetailsService extends AbstractUserDetailsService {

    @Autowired // 不能删掉，不然报错
    PasswordEncoder passwordEncoder;

    @Autowired
    SysUserService sysUserService;

    @Autowired
    SysPermissionService sysPermissionService;

    @Override
    public SysUser findSysUser(String usernameOrMobile) {
        log.info("请求认证的用户名: " + usernameOrMobile);
        // 1. 通过请求的用户名去数据库中查询用户信息
        SysUser sysUserServiceByUsername = sysUserService.findByUsername(usernameOrMobile);
        findSysPermission(sysUserServiceByUsername);
        return sysUserServiceByUsername;

    }

    private void findSysPermission(SysUser sysUser) {
        if(sysUser == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }

        // 2. 查询该用户有哪一些权限
        List<SysPermission> permissions = sysPermissionService.findByUserId(sysUser.getId());

        if(CollectionUtils.isEmpty(permissions)) {
            return ;
        }

        // 在左侧菜单 动态渲染会使用，目前先把它都传入
//        sysUser.setPermissions(permissions);

        // 3. 封装权限信息
        List<GrantedAuthority> authorities = new ArrayList<>();
        for(SysPermission sp: permissions) {
            // 权限标识
            String roleCode = sp.getCode();
            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(roleCode);
            authorities.add(grantedAuthority);
        }
        sysUser.setAuthorities(authorities);
    }

}
