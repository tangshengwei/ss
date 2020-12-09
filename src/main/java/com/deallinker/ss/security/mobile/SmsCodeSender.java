package com.deallinker.ss.security.mobile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 发送短信验证码，第三方的短信服务接口
 * （此类在 SecurityConfigBean 注入了，所以这里不需要加 @Component 注解了，）
 */
//@Component
public class SmsCodeSender implements SmsSend {

    Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * @param mobile 手机号
     * @param content 发送的内容: 接收的是验证码
     * @return
     */
    @Override
    public boolean sendSms(String mobile, String content) {
        String sendContent = String.format("腾讯云，验证码 %s, 请勿泄露给别人。", content);
        // 调用每三方发送功能的sdk
        logger.info("向手机号：" + mobile + "发送的短信为：" + sendContent);
        return true;
    }
}
