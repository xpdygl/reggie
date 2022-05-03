package com.huixu.exception;

/**
 * 自定义异常 -- 解决删除分类时 分类下有菜品或套餐
 */
public class CustomException extends RuntimeException{
    public CustomException(String message) {
        super(message);
    }
}
