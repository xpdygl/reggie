package com.itheima.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.itheima.common.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author XuHui
 * @version 1.0
 */
@Slf4j
@Configuration
public class MyMetaObjectHandler implements MetaObjectHandler {


    @Autowired
    private HttpSession session;
    /**
     * 插入操作 公共字段填充数据设置
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充：【insert】");

        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());


        metaObject.setValue("createUser", BaseContext.get());
        metaObject.setValue("updateUser", BaseContext.get());

    }

    @Override
    public void updateFill(MetaObject metaObject) {

        log.info("公共字段自动填充：【update】");
        log.info("MyMetaObjectHandler-当前线程id：{}",Thread.currentThread().getId());

        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser", BaseContext.get());
    }
}
