package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.bean.Category;
import com.itheima.bean.Dish;
import com.itheima.bean.Setmeal;
import com.itheima.dao.CategoryDao;
import com.itheima.exception.CustomException;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author XuHui
 * @version 1.0
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public void remove(Long id) {
        //1.检查该分类下是否有菜品  dishService
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(dishLambdaQueryWrapper);
        if(count>0){
            //说明该分类下有菜品 不能删除分类 抛出一个自定义的异常
            throw new CustomException("该分类下存在菜品，不能删除！");
        }

        //2.检查该分类下是否有套餐  setmealService
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if(count1>0){
            //说明该分类下有套餐 不能删除分类 抛出一个自定义的异常
            throw new CustomException("该分类下存在套餐，不能删除！");
        }

        //3.如果都没有则删除该分类  removeById在CategoryServiceImpl的父类中存在 可以直接调用
        super.removeById(id);
    }
}
