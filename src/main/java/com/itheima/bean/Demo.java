package com.itheima.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    @Data：打在实体类上  可以为实体类中的所有属性生成getter|setter方法 toString方法 ...
    @Getter|@Setter：
        打在类上：表示为类中所有属性生成对应的getter|setter方法
        打在属性上：只为当前属性生成对应的getter|setter方法
    @ToString：打在实体类上 为类中的所有属性生成toString方法
    @AllArgsConstructor:打在实体类上 为类中的所有属性生成全参构造方法
    @NoArgsConstructor：打在实体类上 为该类生成无参构造方法
    @Slf4j：日志记录 提供了一个隐式的log对象 可以直接使用
 */

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Demo {
    private String name;
    private String age;
}
