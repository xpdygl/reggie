package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.bean.Setmeal;
import com.itheima.dto.SetmealDto;

import java.util.List;

/**
 * @author XuHui
 * @version 1.0
 */
public interface SetmealService extends IService<Setmeal> {
    void saveWithSetmealDish(SetmealDto setmealDto);

    void removeWithSetmealDish(List<Long> ids);
}

