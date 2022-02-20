package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.bean.Category;
import com.itheima.bean.Setmeal;
import com.itheima.common.R;
import com.itheima.dto.SetmealDto;
import com.itheima.service.CategoryService;
import com.itheima.service.SetmealService;
import com.sun.xml.internal.bind.v2.runtime.reflect.Lister;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XuHui
 * @version 1.0
 */
@RestController
@Slf4j
@RequestMapping("/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R save(@RequestBody SetmealDto setmealDto){
        //1.保存套餐

        setmealService.saveWithSetmealDish(setmealDto);

        return R.success("新增成功");
    }



    /*
    在套餐分页查询基本实现返回的是Page<Setmeal>: 总条数：total、每页要显示的数据集合：records=List<Setmeal>
    但是这样返回以后，在显示数据时 没有显示套餐分类名称  所以需要返回的每条数据记录中都包含套餐分类名称categoryName
    此时：就应该返回每页数据集合时：List<SetmealDto>  每一条记录中就有了套餐名称   SetmealDto=Setmeal+categoryName
    做法：
        1.遍历List<Setmeal>  将该集合中的每一个Setmeal都赋值到SetmealDto中
        2.需要根据当前套餐中的category_id查询到category_name 赋值到SetmealDto中
        3.将List<SetmealDto> 重新设置到Page<SetmealDto>中
 */
    @GetMapping("/page")
    public R<Page<SetmealDto>> page(Integer page, Integer pageSize, String name){
//创建分页数据对象
        Page<Setmeal> setmealPage = new Page<Setmeal>(page,pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>();

        //设置分页条件 根据name模糊查询
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);

        //执行分页查询
        setmealService.page(setmealPage,wrapper);

        //数据填充：将每一个setmeal对象中的属性值都复制到SetmealDto对象中 并且设置categoryName属性
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");

        List<Setmeal> setmealList = setmealPage.getRecords();
        ArrayList<SetmealDto> setmealDtoList = new ArrayList<>();

        for (Setmeal setmeal : setmealList) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);

            //设置套餐分类名称
            Category category = categoryService.getById(setmeal.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            setmealDtoList.add(setmealDto);
        }
        setmealDtoPage.setRecords(setmealDtoList);
        //返回
        return R.success(setmealDtoPage);
    }

    @DeleteMapping
    public R delete(@RequestParam List<Long> ids){
        log.info("要删除的套餐ids:{}",ids);
 
        setmealService.removeWithSetmealDish(ids);
        return R.success("套餐删除成功");
    }





}
