package com.deallinker.ss.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * @Description: TODO
 * @Author: tangsw
 * @Date 2020/12/2 13:50
 **/
public class ValidateCodeException extends AuthenticationException {

    private static final long serialVersionUID = 2672899097153524723L;

    public ValidateCodeException(String explanation) {
        super(explanation);
    }
}
