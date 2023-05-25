package com.lixinghao.user.common;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author 风车下跑
 * @create 2023-05-22
 */
@Getter
@Setter
public class BaseResponse<T> implements Serializable {
    private int code;
    private T data;
    private String message;
    private String description;


    public BaseResponse(int code, T data, String message,String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }
    public BaseResponse(String message) {
        this(10400,null,message,"");
    }
    public BaseResponse(int code,String message) {
        this(code,null,message,"");
    }
    public BaseResponse(T data,String message) {
        this(10100,data,message,"");
    }
    public BaseResponse(int code, T data) {
        this(code,data,"","");
    }
    public BaseResponse(T data) {
        this(10100,data,"","");
    }
    public BaseResponse(int code, String message,String description) {
        this(code,message);
        this.description = description;
    }
    public static <T> BaseResponse<T> succsess(T data){
        return new BaseResponse<>(data);
    }
    public static <T> BaseResponse<T> succsess(T data,String message){
        return new BaseResponse<>(data,message);
    }
    public static <T> BaseResponse<T> error(String message){
        return new BaseResponse<>(message);
    }
    public static <T> BaseResponse<T> error(ErrorCode errorCode){
        return new BaseResponse<>(errorCode.getCode(), errorCode.getMessage());
    }
    public static <T> BaseResponse<T> error(int code,String message,String description){
        return new BaseResponse(code, message,description);
    }
}
