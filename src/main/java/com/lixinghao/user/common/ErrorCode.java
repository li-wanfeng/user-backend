package com.lixinghao.user.common;

import lombok.Getter;
import lombok.Setter;

/**
 * @author 风车下跑
 * @create 2023-05-22
 */
@Getter
public enum ErrorCode {
    ACCOUNT_EXITS(10401,"账号已存在"),
    PARAM_ERROR(10402,"参数错误"),
    DELETE_ERROR(10403,"删除失败"),
    INSERT_ERROR(10406,"插入失败"),
    USER_NOT_LOGIN(10404,"用户未登录"),
    NOT_ADMIN(10405,"非管理权限，拒绝访问");
    private int code;
    private String message;
    private String description;

    ErrorCode(int code, String message,String description) {
        this.code = code;
        this.message = message;
        this.description = description;
    }

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
        this.description = "";
    }
}
