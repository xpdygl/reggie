package com.huixu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huixu.bean.Category;
import com.huixu.bean.Setmeal;
import com.huixu.common.R;
import com.huixu.dto.SetmealDto;
import com.huixu.service.CategoryService;
import com.huixu.service.SetmealService;
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

        //2.设置分页查询条件 根据name进行模糊查询
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Setmeal::getName,name);
        //3.执行分页查询
        setmealService.page(setmealPage,wrapper);

        //数据填充：将每一个setmeal对象中的属性值都复制到SetmealDto对象中
        // 并且设置categoryName属性
        BeanUtils.copyProperties(setmealPage,setmealDtoPage,"records");


        List<Setmeal> setmealList = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = new ArrayList<>();
        for (Setmeal setmeal : setmealList) {
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(setmeal,setmealDto);

            //设置套餐分类名称

            //第一句通过setmeal拿到Category的ID 然后用categoryService.getById
            //获得category对象
            //第二句 用category对象的名字赋值给setmealDto
            //dto 普通的类字段更多
            //第三句 在循环中将每个setmealDto 给到setmealDtoList中
            //完成赋值
            Category category = categoryService.getById(setmeal.getCategoryId());
            setmealDto.setCategoryName(category.getName());
            setmealDtoList.add(setmealDto);
        }
        setmealDtoPage.setRecords(setmealDtoList);



        //4.返回分页数据 Page 封装到R对象中
        return R.success(setmealDtoPage);
    }

    @DeleteMapping
    public R delete(@RequestParam List<Long> ids){
        log.info("要删除的套餐ids:{}",ids);
 
        setmealService.removeWithSetmealDish(ids);
        return R.success("套餐删除成功");
    }


    @GetMapping("/list")
   public  R list(Setmeal setmeal){
        LambdaQueryWrapper<Setmeal> wrapper = new LambdaQueryWrapper<>();
        //2.设置套餐所属的分类  categoryId

        wrapper.eq(setmeal.getCategoryId()!=null,Setmeal::getCategoryId,setmeal.getCategoryId());
        //3.设置当前套餐的状态  在售：status=1
        wrapper.eq(setmeal.getStatus()!=null,Setmeal::getStatus,setmeal.getStatus());
        List<Setmeal> list = setmealService.list(wrapper);
        return R.success(list);
    }

    /**
     * Request URL: http://localhost/setmeal/status/0?ids=1415580119015145474,1494282338281689090
     * Request Method: POST
     * Status Code: 404
     */
    @PostMapping("/status/{status}")
    public R<String> deleteById (@PathVariable Integer status,@RequestParam List<Long> ids){

        LambdaUpdateWrapper<Setmeal> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Setmeal::getId,ids);

        wrapper.set(Setmeal::getStatus,status);
        setmealService.update(wrapper);
        return R.success("套餐信息修改成功");
        }







}
