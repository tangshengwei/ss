package com.deallinker.ss.security.verifycode;

import com.deallinker.ss.security.exception.ValidateCodeException;
import com.deallinker.ss.security.handler.CustomAuthenticationFailureHandler;
import com.deallinker.ss.security.properties.SecurityProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @Description: 验证码校验拦截器，如果短信登录会先走此方法，在 SpringSecurityConfig 中配置的
 * @Author: tangsw
 * @Date 2020/12/2 13:54
 **/
@Component
public class ValidateCodeFilter extends OncePerRequestFilter {

    @Autowired
    private CustomAuthenticationFailureHandler authenticationFailureHandler;

    @Autowired
    private SecurityProperties securityProperties;

    private SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();


    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (StringUtils.equalsIgnoreCase(securityProperties.getAuthentication().getLoginProcessingUrl(), httpServletRequest.getRequestURI())
                && StringUtils.equalsIgnoreCase(httpServletRequest.getMethod(), "post")) {
            try {
//                validateCode(new ServletWebRequest(httpServletRequest));
                  validateCode(httpServletRequest);
            } catch (ValidateCodeException e) {
                e.printStackTrace();
                authenticationFailureHandler.onAuthenticationFailure(httpServletRequest, httpServletResponse, e);
                return;
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private void validateCode(HttpServletRequest request) throws ValidateCodeException {
//        ImageCode codeInSession = (ImageCode) sessionStrategy.getAttribute(servletWebRequest, ValidateController.SESSION_KEY);
//        String codeInRequest = ServletRequestUtils.getStringParameter(servletWebRequest.getRequest(), "imageCode");

        // 先获取seesion中的验证码
        String codeInSession = (String)request.getSession().getAttribute(ValidateController.SESSION_KEY);
        // 获取用户输入的验证码
        String codeInRequest = request.getParameter("imageCode");

        if (StringUtils.isBlank(codeInRequest)) {
            throw new ValidateCodeException("验证码不能为空！");
        }
        if (codeInSession == null) {
            throw new ValidateCodeException("验证码不存在！");
        }
//        if (codeInSession.isExpire()) {
//            sessionStrategy.removeAttribute(servletWebRequest, ValidateController.SESSION_KEY);
//            throw new ValidateCodeException("验证码已过期！");
//        }
        if (!StringUtils.equalsIgnoreCase(codeInSession, codeInRequest)) {
            throw new ValidateCodeException("验证码不正确！");
        }
//        sessionStrategy.removeAttribute(servletWebRequest, ValidateController.SESSION_KEY);
        request.getSession().removeAttribute(ValidateController.SESSION_KEY);
    }
}
