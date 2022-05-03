package com.huixu.dto;


import com.huixu.bean.Setmeal;
import com.huixu.bean.SetmealDish;
import lombok.Data;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
