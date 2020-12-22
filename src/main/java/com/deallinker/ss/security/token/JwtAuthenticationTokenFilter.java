package com.deallinker.ss.security.token;

import com.deallinker.ss.utils.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 确保经过filter为一次请求
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private final static String HEADER = "Authorization";

    @Autowired
    private TokenUserDetailsService tokenUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String token = request.getHeader(HEADER);
        if (StringUtils.isBlank(token)) {
            chain.doFilter(request, response);
            return;
        }
        if(JwtTokenUtil.isTokenExpired(token)){
            getResponse(response,"token过期！");
            return;
        }
        String username = JwtTokenUtil.getUsernameFromToken(token);
        if(username == null || username == ""){
            getResponse(response,"token错误！");
            return;
        }
        //把用户的信息填充到上下文中
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = tokenUserDetailsService.findSysUser(username);
            if (userDetails != null) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        logger.info("checking authentication " + username);
        chain.doFilter(request, response);
    }

    /**
     *  组装token验证失败的返回
     * @param response
     * @param msg
     * @return
     */
    private HttpServletResponse getResponse(HttpServletResponse response, String msg){
        Response result = Response.build(HttpServletResponse.SC_FORBIDDEN, msg);
        response.setContentType("Application/json;charset=UTF-8");
        try {
            response.getWriter().write(result.toJsonString());
        }catch (Exception o){
            o.printStackTrace();
        }
        return response;
    }

}
