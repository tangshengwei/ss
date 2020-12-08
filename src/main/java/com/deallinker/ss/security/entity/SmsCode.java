package com.deallinker.ss.security.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @Description: TODO
 * @Author: tangsw
 * @Date 2020/12/2 15:00
 **/
@Data
public class SmsCode {
    private String code;
    private LocalDateTime expireTime;

    public SmsCode(String code, int expireIn) {
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    public SmsCode(String code, LocalDateTime expireTime) {
        this.code = code;
        this.expireTime = expireTime;
    }

    boolean isExpire() {
        return LocalDateTime.now().isAfter(expireTime);
    }

}
