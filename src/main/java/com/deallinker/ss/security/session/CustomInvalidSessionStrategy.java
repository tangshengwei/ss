package com.deallinker.ss.security.session;

import com.deallinker.ss.utils.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.InvalidSessionStrategy;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 当session失效后的处理逻辑
 * （失效时间在 application.xml 中配置、可以指定存储位置）
 */

public class CustomInvalidSessionStrategy implements InvalidSessionStrategy {
    Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private SessionRegistry sessionRegistry;

    @Override
    public void onInvalidSessionDetected(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // session 中的 sessionID 如果不存在会生成一个
        logger.info("getSession().getId(): " + request.getSession().getId());
        // 浏览器请求的 sessionId，
        logger.info("getRequestedSessionId(): " + request.getRequestedSessionId());
        // 缓存中移除session信息, 返回客户端的 sessionId 值
        sessionRegistry.removeSessionInformation(request.getRequestedSessionId());

        // 要将浏览器中的cookie的jsessionid删除
        cancelCookie(request, response);

        Response result = Response.build(
                HttpStatus.UNAUTHORIZED.value(), "登录已超时，请重新登录");

        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(result.toJsonString());
    }

    protected void cancelCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath(getCookiePath(request));
        response.addCookie(cookie);
    }

    private String getCookiePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return contextPath.length() > 0 ? contextPath : "/";
    }
}
