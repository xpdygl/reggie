package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.bean.Dish;
import com.itheima.dto.DishDto;

import java.util.List;

/**
 * @author XuHui
 * @version 1.0
 */
public interface DishService extends IService<Dish> {

    /**
     * 保存菜品及其口味信息
     * @param dishDto
     */
    void saveWithFlavor(DishDto dishDto);


    /**
     * 根据菜品id查询菜品及其口味信息
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 修改菜品及其口味信息
     * @param dishDto
     */
    void updateWithFlavor(DishDto dishDto);


    /**
     * 删除菜品
     * @param ids
     */
//    void  remove (Long ids);

//    void remove(List<Dish> ids);
}
