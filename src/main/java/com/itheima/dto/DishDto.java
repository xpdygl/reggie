package com.itheima.dto;

import com.itheima.bean.Dish;
import com.itheima.bean.DishFlavor;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

//这个注解的作用 是设置打印对象属性时 打印继承父类的属性信息
@ToString(callSuper = true)
@Data
public class DishDto extends Dish {

    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
