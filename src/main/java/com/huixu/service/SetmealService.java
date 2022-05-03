package com.huixu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huixu.bean.Setmeal;
import com.huixu.dto.SetmealDto;

import java.util.List;

/**
 * @author XuHui
 * @version 1.0
 */
public interface SetmealService extends IService<Setmeal> {
    void saveWithSetmealDish(SetmealDto setmealDto);

    void removeWithSetmealDish(List<Long> ids);
}

