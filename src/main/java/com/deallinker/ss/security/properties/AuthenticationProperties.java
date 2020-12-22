package com.deallinker.ss.security.properties;

import lombok.Data;

/**
 * Spring security 配置
 */
@Data
public class AuthenticationProperties {

    private String loginPage;
    private String loginProcessingUrl;
    private String usernameParameter;
    private String passwordParameter;
    private String[] staticPaths;

    /**
     * 认证响应的类型： JSON 、 REDIRECT 重定向
     */
    private LoginResponseType loginType;

    /**
     *  获取图形验证码地址
     */
    private String imageCodeUrl;
    /**
     * # 发送手机验证码地址
     */
    private String mobileCodeUrl;
    /**
     * # 前往手机登录页面
     */
    private String mobilePage;
    /**
     * # 记住我功能有效时长
     */
    private Integer tokenValiditySeconds;

    /**
     * 退出请求路径，默认 /logout
     */
    private String logoutUrl;

    /**
     * 退出成功后跳转地址
     */
    private String logoutSuccessUrl;

    /**
     * 退出后删除什么cookie值
     */
    private String deleteCookies;

    /**
     * 手机短信验证码登录地址
     */
    private String smsLogin;

}
