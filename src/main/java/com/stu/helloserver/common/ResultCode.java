package com.stu.helloserver.common;

public enum ResultCode {

    // 基础状态码
    SUCCESS(200, "操作成功"),
    ERROR(500, "系统繁忙，请稍后再试"),

    // 权限相关
    TOKEN_INVALID(401, "登录凭证已缺失或过期，请重新登录"),

    // 用户相关
    USER_HAS_EXISTED(400, "用户名已存在"),
    USER_NOT_EXIST(404, "用户不存在"),
    PASSWORD_ERROR(401, "密码错误");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}