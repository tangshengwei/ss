package com.deallinker.ss.security.handler;

import com.alibaba.fastjson.JSON;
import com.deallinker.ss.base.entity.SysUser;
import com.deallinker.ss.security.properties.LoginResponseType;
import com.deallinker.ss.security.properties.SecurityProperties;
import com.deallinker.ss.security.token.JwtTokenUtil;
import com.deallinker.ss.utils.Response;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 认证成功 处理器
 * @Author: tangsw
 * @Date 2020/12/2 17:06
 **/
@Component
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    SecurityProperties securityProperties;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        logger.info("登录成功{}", authentication);

        if(LoginResponseType.JSON.equals(
                securityProperties.getAuthentication().getLoginType())) {

            SysUser userDetails = (SysUser)authentication.getPrincipal();
            SecurityContextHolder.getContext().setAuthentication(authentication);
            //生成token
            String token = JwtTokenUtil.generateToken(userDetails);
            Response result = Response.ok("认证成功", token);
            response.setContentType("Application/json;charset=UTF-8");
            response.getWriter().write(result.toJsonString());
        }else {
            //重定向到上次请求的地址上，引发跳转到认证页面的地址
            logger.info("authentication: " + JSON.toJSONString(authentication));
            super.onAuthenticationSuccess(request, response, authentication);
        }

    }
}
