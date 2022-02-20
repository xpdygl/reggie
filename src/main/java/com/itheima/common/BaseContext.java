package com.itheima.common;

/**
 * 基于ThreadLocal工具类  在同一线程间共享数据
 */

/**
 * @author XuHui
 * @version 1.0
 */
public class BaseContext {
    private static final ThreadLocal<Long> THREAD_LOCAL_EMPID = new ThreadLocal<>();

    public static void set(Long id){
        THREAD_LOCAL_EMPID.set(id);
    }

    public static Long get(){
        return THREAD_LOCAL_EMPID.get();
    }
}
