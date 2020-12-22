package com.deallinker.ss.security.config;

import com.deallinker.ss.security.handler.*;
import com.deallinker.ss.security.mobile.MobileCodeAuthenticationSecurityConfig;
import com.deallinker.ss.security.mobile.MobileValidateFilter;
import com.deallinker.ss.security.properties.SecurityProperties;
import com.deallinker.ss.security.token.JwtAuthenticationTokenFilter;
import com.deallinker.ss.security.userdetail.CustomUserDetailsService;
import com.deallinker.ss.security.verifycode.ValidateCodeFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsUtils;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Autowired
    private CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    private MobileCodeAuthenticationSecurityConfig smsCodeAuthenticationSecurityConfig;

    @Autowired
    private SecurityProperties securityProperties;
    @Autowired
    MyAccessDeniedHandler myAccessDeniedHandler;
    @Autowired
    MyAuthenticationEntryPoint myAuthenticationEntryPoint;
    @Autowired
    MyLogoutSuccessHandler myLogoutSuccessHandler;


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService).passwordEncoder(new PasswordEncoder() {
//            @Override
//            public String encode(CharSequence charSequence) {
//                return charSequence.toString();
//            }
//
//            @Override
//            public boolean matches(CharSequence charSequence, String s) {
//                return s.equals(charSequence.toString());
//            }
//        });

        auth.userDetailsService(userDetailsService); //.passwordEncoder(new BCryptPasswordEncoder());
    }

    @Autowired
    JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
    @Autowired
    ValidateCodeFilter validateCodeFilter;
    @Autowired
    MobileValidateFilter mobileValidateFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // token 登录
                .addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class)
//                // 手机验证码
                .addFilterBefore(mobileValidateFilter, UsernamePasswordAuthenticationFilter.class)
//                // 图片验证码
                .addFilterBefore(validateCodeFilter, UsernamePasswordAuthenticationFilter.class)
                // 表单登录方式
                .formLogin()
                // 输入用户名密码登录页面
                .loginPage(securityProperties.getAuthentication().getLoginPage())
                // 登录表单提交处理url, 默认是/login
                .loginProcessingUrl(securityProperties.getAuthentication().getLoginProcessingUrl())
                // 设置登陆成功页
//                .defaultSuccessUrl("/")
                // 默认的是 username
                .usernameParameter(securityProperties.getAuthentication().getUsernameParameter())
                // 默认的是 password
                .passwordParameter(securityProperties.getAuthentication().getPasswordParameter())
                // 登录失败处理器
                .failureHandler(customAuthenticationFailureHandler)
                // 登录成功处理器
                .successHandler(customAuthenticationSuccessHandler)
                .and()
                .authorizeRequests()
                // 不需要认证就能访问页面
                .antMatchers(
                        securityProperties.getAuthentication().getLoginPage(),
                        securityProperties.getAuthentication().getImageCodeUrl(),
                        securityProperties.getAuthentication().getMobileCodeUrl()
                ).permitAll()
                /** 解决跨域 **/
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                // 任何请求都要认证
                .anyRequest().authenticated()
                .and()
                .logout()
                // 退出请求路径，默认 /logout
                .logoutUrl(securityProperties.getAuthentication().getLogoutUrl())
                // 退出成功后跳转地址
//                .logoutSuccessUrl(securityProperties.getAuthentication().getLogoutSuccessUrl())
                // 退出成功 handler
                .logoutSuccessHandler(myLogoutSuccessHandler);

        // 将手机认证添加到过滤器链上
        http.apply(smsCodeAuthenticationSecurityConfig);

        // 新加入(cors) CSRF  取消跨站请求伪造防护
        http
                .cors().and().csrf().disable()
                // 使用 JWT，关闭token
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http
                .exceptionHandling()
                // 没有权限处理的类
                .accessDeniedHandler(myAccessDeniedHandler)
                // 用户未登录时返回给前端的数据
                .authenticationEntryPoint(myAuthenticationEntryPoint)
        ;
    }



    @Override
    public void configure(WebSecurity web) throws Exception {
        // 设置拦截忽略文件夹，可以对静态资源放行
        web.ignoring().antMatchers(securityProperties.getAuthentication().getStaticPaths());
    }
}

