package com.deallinker.ss.security.mobile;

/**
 * @Description: TODO
 * @Author: tangsw
 * @Date 2020/12/2 17:12
 **/

import com.deallinker.ss.security.handler.CustomAuthenticationFailureHandler;
import com.deallinker.ss.security.handler.CustomAuthenticationSuccessHandler;
import com.deallinker.ss.security.userdetail.MobileUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.SecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.stereotype.Component;

/**
 * 用于组合其他关于手机登录的组件
 * @Auther: 梦学谷 www.mengxuegu.com
 */
@Component
public class MobileCodeAuthenticationSecurityConfig extends SecurityConfigurerAdapter<DefaultSecurityFilterChain, HttpSecurity> {
    @Autowired
    private MobileUserDetailsService mobileUserDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        MobileCodeAuthenticationFilter smsCodeAuthenticationFilter = new MobileCodeAuthenticationFilter();

        // 获取容器中已经存在的AuthenticationManager对象，并传入 mobileAuthenticationFilter 里面
        smsCodeAuthenticationFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));

        // 指定记住我功能
        smsCodeAuthenticationFilter.setRememberMeServices(http.getSharedObject(RememberMeServices.class));

        // session重复登录 管理（用账号密码登录了，同一个账号手机号也不能登录了）
        smsCodeAuthenticationFilter.setSessionAuthenticationStrategy(
                http.getSharedObject(SessionAuthenticationStrategy.class));

        // 传入 失败与成功处理器
        smsCodeAuthenticationFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler);
        smsCodeAuthenticationFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler);

        // 构建一个MobileAuthenticationProvider实例，接收 mobileUserDetailsService 通过手机号查询用户信息
        MobileCodeAuthenticationProvider smsCodeAuthenticationProvider = new MobileCodeAuthenticationProvider();
        smsCodeAuthenticationProvider.setUserDetailsService(mobileUserDetailsService);

        // 将provider绑定到 HttpSecurity上，并将 手机号认证过滤器绑定到用户名密码认证过滤器之后
        http.authenticationProvider(smsCodeAuthenticationProvider)
                .addFilterAfter(smsCodeAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
