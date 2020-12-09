package com.deallinker.ss.security.userdetail;


import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.deallinker.ss.base.entity.SysPermission;
import com.deallinker.ss.base.entity.SysUser;
import com.deallinker.ss.base.service.SysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public abstract class AbstractUserDetailsService implements UserDetailsService {

    @Autowired
    private SysPermissionService sysPermissionService;

    /**
     * 这个方法交给子类去实现它，查询用户信息
     * @param usernameOrMobile 用户名或者手机号
     * @return
     */
    public abstract SysUser findSysUser(String usernameOrMobile);

    @Override
    public UserDetails loadUserByUsername(String usernameOrMobile) throws UsernameNotFoundException {
        // 1. 通过请求的用户名去数据库中查询用户信息
        SysUser sysUser = findSysUser(usernameOrMobile);
        // 2. 通过用户id去获取权限信息
        findSysPermission(sysUser);

        return sysUser;
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
