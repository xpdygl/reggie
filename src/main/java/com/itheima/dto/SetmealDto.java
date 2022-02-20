package com.itheima.dto;


import com.itheima.bean.Setmeal;
import com.itheima.bean.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
