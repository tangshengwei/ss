package com.deallinker.ss.security.mobile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;


/**
 * 发送短信验证码，第三方的短信服务接口
 */
@Component
public class SmsCodeSendByALi implements SmsSend {

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param mobile 手机号
     * @param content 发送的内容: 接收的是验证码
     * @return
     */
    @Override
    public boolean sendSms(String mobile, String content) {
        String sendContent = String.format("阿里云，验证码 %s, 请勿泄露给别人。", content);
        // 调用每三方发送功能的sdk
        logger.info("向手机号：" + mobile + "发送的短信为：" + sendContent);
        return true;
    }
}
