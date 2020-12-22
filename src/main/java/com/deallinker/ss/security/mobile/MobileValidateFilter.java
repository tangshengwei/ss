package com.deallinker.ss.security.mobile;

import com.deallinker.ss.security.exception.ValidateCodeException;
import com.deallinker.ss.security.handler.CustomAuthenticationFailureHandler;
import com.deallinker.ss.security.properties.SecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 校验用户输入的手机验证码是否正确
 *
 */
@Component
public class MobileValidateFilter extends OncePerRequestFilter {


    @Autowired
    CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    @Autowired
    SecurityProperties securityProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 判断 请求是否为手机登录，且post请求
        if(securityProperties.getAuthentication().getSmsLogin().equals(request.getRequestURI())
            && "post".equalsIgnoreCase(request.getMethod())) {
            try {
                // 校验验证码合法性
                validate(request);
            }catch (AuthenticationException e) {
                e.printStackTrace();
                // 交给失败处理器进行处理异常
                customAuthenticationFailureHandler.onAuthenticationFailure(request, response, e);
                // 一定要记得结束
                return;
            }
        }

        // 放行
        filterChain.doFilter(request, response);
    }

    private void validate(HttpServletRequest request) {
        // 先获取seesion中的验证码
        Map<String, Object> smsCode = (Map<String, Object>) request.getSession().getAttribute("smsCode");

        // 判断是否正确
        if(smsCode == null) {
            throw new ValidateCodeException("验证码已经失效请重新获取");
        }

        // 系统生成验证码
        String sessionCode = smsCode.get("code").toString();
        String sessionMobile = smsCode.get("mobile").toString();
        // 获取用户输入的验证码
        String inpuCode = request.getParameter("smsCode");
        String inpuMobile = request.getParameter("mobile");

        if(!inpuCode.equalsIgnoreCase(sessionCode)) {
            throw new ValidateCodeException("验证码输入错误");
        }
        if(!inpuMobile.equals(sessionMobile)) {
            throw new BadCredentialsException("申请的手机号码与登录手机号码不一致");
        }
    }
}
