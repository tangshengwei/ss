package com.deallinker.ss.security.mobile;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * 短信登陆鉴权 Provider，要求实现 AuthenticationProvider 接口
 */
public class MobileCodeAuthenticationProvider implements AuthenticationProvider {
    private UserDetailsService userDetailsService;

    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        MobileCodeAuthenticationToken authenticationToken = (MobileCodeAuthenticationToken) authentication;

        String mobile = (String) authenticationToken.getPrincipal();

        UserDetails userDetails = userDetailsService.loadUserByUsername(mobile);

        // 未查询到用户信息
        if(userDetails == null) {
            throw new AuthenticationServiceException("该手机号未注册");
        }

        // 此时鉴权成功后，应当重新 new 一个拥有鉴权的 authenticationResult 返回
        MobileCodeAuthenticationToken authenticationResult = new MobileCodeAuthenticationToken(userDetails, userDetails.getAuthorities());
        authenticationResult.setDetails(authenticationToken.getDetails());

        return authenticationResult;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        // 判断 authentication 是不是 MobileCodeAuthenticationToken 的子类或子接口
        return MobileCodeAuthenticationToken.class.isAssignableFrom(authentication);
    }

//    public UserDetailsService getUserDetailsService() {
//        return userDetailsService;
//    }


}
