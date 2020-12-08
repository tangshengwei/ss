package com.deallinker.ss.security.session;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Auther: 梦学谷 www.mengxuegu.com
 */
@Component
public class CustomLogoutHandler implements LogoutHandler {

    @Autowired
    private SessionRegistry sessionRegistry;

    @Override
    public void logout(HttpServletRequest request,
                       HttpServletResponse response,
                       Authentication authentication) {
        // 退出之后 ，将对应session从缓存中清除 SessionRegistryImpl.principals
        // 解决：只允许一个用户登录，用户退出后，还提示登录问题
        sessionRegistry.removeSessionInformation(request.getSession().getId());
    }
}
