package com.deallinker.ss.security.handler;

import com.deallinker.ss.security.properties.LoginResponseType;
import com.deallinker.ss.security.properties.SecurityProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SecurityProperties securityProperties;

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        logger.info("登陆失败");

        if(LoginResponseType.JSON.equals(securityProperties.getAuthentication().getLoginType())) {
            // 认证失败响应JSON字符串，
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(objectMapper.writeValueAsString(exception.getMessage()));
        }else {
            // 重写向回认证页面，注意加上 ?error
//            super.setDefaultFailureUrl(securityProperties.getAuthentication().getLoginPage()+"?error");
            // 获取上一次请求路径
            String referer = request.getHeader("Referer");
            logger.info("referer:" + referer);

            // 如果下面有值,则认为是多端登录,直接返回一个登录地址
            Object toAuthentication = request.getAttribute("toAuthentication");
            String lastUrl = toAuthentication != null ? securityProperties.getAuthentication().getLoginPage()
                    : StringUtils.substringBefore(referer,"?");

            logger.info("上一次请求的路径 ：{}" ,lastUrl);
            super.setDefaultFailureUrl(lastUrl+"?error");
            super.onAuthenticationFailure(request, response, exception);
        }

    }
}
