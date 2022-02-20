package com.itheima.exception;

import com.itheima.common.R;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @author XuHui
 * @version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {


    //捕获指定异常 处理  java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'zhangsan' for key 'idx_username'
    //你要捕获什么异常 进行处理  在这个注解中就写什么异常类型

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    public R<String> exceptionHandler(SQLIntegrityConstraintViolationException exception){
        if (exception.getMessage().contains("Duplicate entry")){
            String message = exception.getMessage();
            String[] s = message.split(" ");
            return R.error(s[2]+"已存在！");
        }
        return R.error("未知错误！");
    }





    @ExceptionHandler(CustomException.class)
    public R<String> exceptionHandler(CustomException exception){

        return R.error(exception.getMessage());
    }
}
