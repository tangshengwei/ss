package com.deallinker.ss.utils;

import com.alibaba.fastjson.JSON;
import lombok.Data;

/**
 * 自定义响应结构
 */
@Data
public class Response {

    // 响应业务状态
    private Integer code;

    // 响应消息
    private String message;

    // 响应中的数据
    private Object data;

    public Response() {
    }
    public Response(Object data) {
        this.code = 200;
        this.message = "OK";
        this.data = data;
    }
    public Response(String message, Object data) {
        this.code = 200;
        this.message = message;
        this.data = data;
    }

    public Response(Integer code, String message, Object data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static Response ok() {
        return new Response(null);
    }
    public static Response ok(String message) {
        return new Response(message, null);
    }
    public static Response ok(Object data) {
        return new Response(data);
    }
    public static Response ok(String message, Object data) {
        return new Response(message, data);
    }

    public static Response build(Integer code, String message) {
        return new Response(code, message, null);
    }

    public static Response build(Integer code, String message, Object data) {
        return new Response(code, message, data);
    }

    public String toJsonString() {
        return JSON.toJSONString(this);
    }


    /**
     * JSON字符串转成 Response 对象
     * @param json
     * @return
     */
    public static Response format(String json) {
        try {
            return JSON.parseObject(json, Response.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
