package com.blindbox.common;

import lombok.Getter;

@Getter
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    PARAM_ERROR(400, "参数错误"),
    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "没有权限"),
    NOT_FOUND(404, "资源不存在"),
    LOGIN_ERROR(1001, "用户名或密码错误"),
    USER_DISABLED(1002, "账号已被禁用"),
    TOKEN_INVALID(1003, "token无效"),
    MACHINE_OFFLINE(2001, "机器离线"),
    MACHINE_FAULT(2002, "机器故障"),
    INVENTORY_NOT_ENOUGH(2003, "库存不足"),
    DRAW_FAILED(2004, "抽盒失败"),
    SHIP_FAILED(2005, "出货失败"),
    ORDER_NOT_EXIST(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态错误"),
    DUPLICATE_PAY(3003, "重复支付");

    private final Integer code;
    private final String msg;

    ResultCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
