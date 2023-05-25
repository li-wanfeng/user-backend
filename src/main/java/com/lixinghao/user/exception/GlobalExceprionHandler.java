package com.lixinghao.user.exception;

import com.lixinghao.user.common.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author 风车下跑
 * @create 2023-05-22
 */
@RestControllerAdvice//这里利用了SpringAOP机制，在错误的地方进行切面，之前，之后处理
public class GlobalExceprionHandler {

    @ExceptionHandler(BusInessException.class)//这个注解参数传入一个数组，代表只针对哪个异常有效
    public BaseResponse busInessExceptionHandler(BusInessException e){
        return BaseResponse.error(e.getCode(),e.getMessage(),e.getDescription());
    }
    //这种的异常处理方法可以定制多个，但是要注意，小的异常放在最前面，大的异常，例如Exception放在最后面
    @ExceptionHandler(Exception.class)
    public BaseResponse exceptionHandler(Exception e){
        return BaseResponse.error(e.getMessage());
    }
}

