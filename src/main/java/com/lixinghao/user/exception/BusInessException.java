package com.lixinghao.user.exception;

import com.lixinghao.user.common.ErrorCode;
import lombok.Getter;

/**
 * @author 风车下跑
 * @create 2023-05-22
 */

@Getter
public class BusInessException extends RuntimeException{
    private final int code;//状态码信息
    private final String description; //更详细的描述

    public BusInessException(String message, int code,String description) {
        super(message);
        this.code = code;
        this.description = description;
    }
    public BusInessException(String message, int code) {
        super(message);
        this.code = code;
        this.description = "";
    }

    public BusInessException(ErrorCode errorCode,String description) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
    public BusInessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description ="";
    }
}
