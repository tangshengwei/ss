package com.deallinker.ss.base.controller;

import com.deallinker.ss.security.mobile.SmsSend;
import com.deallinker.ss.utils.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @Author: tangsw
 * @Date 2020/12/2 17:21
 **/
@Slf4j
@Controller
public class MobileController {

    @Autowired
    private SmsSend smsSend;

    @RequestMapping("/code/mobile")
    @ResponseBody
    public Response sms(String mobile, HttpSession session) {
        int code = (int) Math.ceil(Math.random() * 9000 + 1000);

        Map<String, Object> map = new HashMap<>(16);
        map.put("mobile", mobile);
        map.put("code", code);

        session.setAttribute("smsCode", map);

        smsSend.sendSms(mobile, String.valueOf(code));
        return Response.ok("短信验证码发送成功");
    }

}
