package com.itheima;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author XuHui
 * @version 1.0
 */
@Slf4j
@ServletComponentScan
/*在spring中通过这个注解扫描到过滤器以及servlet*/
@SpringBootApplication
@EnableTransactionManagement
@EnableCaching
//开启缓存注解支持
public class ReggieApplication {
    public static void main(String[] args) {
        SpringApplication.run(ReggieApplication.class,args);
        log.info("徐晖项目启动成功！");
    }
}
/*
 @Slf4j
   @SpringBootApplication
   public class ReggieApplication {
    public static void main(String[] args) {
     SpringApplication.run(ReggieApplication.class,args);
     log.info("项目启动成功！");
    }
   }

 */