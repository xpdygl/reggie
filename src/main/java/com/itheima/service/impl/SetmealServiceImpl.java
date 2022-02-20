package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.bean.Setmeal;
import com.itheima.bean.SetmealDish;
import com.itheima.dao.SetmealDao;
import com.itheima.dto.SetmealDto;
import com.itheima.exception.CustomException;
import com.itheima.service.SetmealDishService;
import com.itheima.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author XuHui
 * @version 1.0
 */
@Service
@Transactional
public class SetmealServiceImpl extends ServiceImpl<SetmealDao, Setmeal> implements SetmealService {
    @Autowired
    private SetmealDishService setmealDishService;
    @Override
    public void saveWithSetmealDish(SetmealDto setmealDto) {
        //1.保存套餐基本信息到setmeal表
        this.save(setmealDto);
        //2.保存套餐下的菜品信息到setmeal_dish表中
        Long setmealid = setmealDto.getId();
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

        for (SetmealDish setmealDish : setmealDishes) {
            setmealDish.setSetmealId(setmealid);
        }

        //2.3：批量保存套餐菜品信息到setmeal_dish表中
        setmealDishService.saveBatch(setmealDishes);

    }

    @Override
    @Transactional
    public void removeWithSetmealDish(List<Long> ids) {
        if (ids == null || ids.size()==0){
            throw new CustomException("要删除的套餐不能为空");
        }
        //2.1：首先需要查询要删除的套餐是否在售  如果在售 则不能删除【抛一个自定义异常  交给全局异常处理器处理】
        LambdaQueryWrapper<Setmeal> setmealLambda = new LambdaQueryWrapper<>();
        //设置查询条件
        setmealLambda.eq(Setmeal::getStatus,1);
        //设置在售状态  status 1：在售  0：停售
        setmealLambda.in(Setmeal::getId,ids);
        //设置要查询哪些套餐的在售状态  根据套餐id查询
        int count = this.count(setmealLambda);
        if (count>0){
            throw new CustomException("要删除的套餐在售，不能删除！");
        }


        //2.2：先删除setmeal表中的套餐信息记录
        //这种做法不合理  会在添加异常status字段
        /*setmealLambdaQueryWrapper.eq(Setmeal::getStatus,0);  //重新设置之前的条件  status为0  表示停售 才可以删除
        this.remove(setmealLambdaQueryWrapper);*/
        //使用这种做法  直接根据多个id批量删除
        this.removeByIds(ids);


        //2.3：再根据当前套餐id 去到setmeal_dish套餐菜品关系表中 删除该套餐下的所有菜品信息
        //设置要删除的菜品所属的套餐id
        LambdaQueryWrapper<SetmealDish> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(wrapper);
    }
}
