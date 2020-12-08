package com.deallinker.ss.security.verifycode;

import lombok.Data;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @Description: TODO
 * @Author: tangsw
 * @Date 2020/12/2 13:15
 **/
@Data
public class ImageCode implements Serializable {

    private BufferedImage image;
    private String code;
    private LocalDateTime expireTime;

    public ImageCode(BufferedImage image, String code, int expireIn) {
        this.image = image;
        this.code = code;
        this.expireTime = LocalDateTime.now().plusSeconds(expireIn);
    }

    boolean isExpire() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
