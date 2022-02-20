package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.bean.Category;
import com.itheima.bean.Dish;
import com.itheima.common.R;
import com.itheima.dto.DishDto;
import com.itheima.service.CategoryService;
import com.itheima.service.DishService;
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
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public R save(@RequestBody DishDto dishDto){
        log.info("dish:{}",dishDto);
        dishService.saveWithFlavor(dishDto);
        return R.success("菜品增加成功！");

    }


    @GetMapping("/page")
    public R page(Integer page,Integer pageSize,String name){
        //0.配置MyBatisPlus分页拦截器【已完成】
        //1.设置分页条件 【当前页码和每页显示条数】  使用Page对象设置
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //优化步骤1：返回的数据应该封装到Page<DishDto>中
        Page<DishDto> dishDtoPage = new Page<DishDto>();

        //2.设置分页查询条件 【菜品名称name 模糊查询 有就设置  没有就不设置】
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Dish::getName,name);

        //3.执行分页查询  查询之后会将total、records重新填入到Page对象中
        dishService.page(dishPage,dishLambdaQueryWrapper);

        //优化步骤2：需要将查询出来的菜品分页信息数据dishPage封装到dishDtoPage中   dishDtoPage=dishPage+categoryName
        //因为dishPage.getRecords得到的是List<Dish>当前页数据  在数据中没有菜品分类名称  需要得到当前菜品信息 List<Dish>添加上分类名称后存入到List<DishDto>
        BeanUtils.copyProperties(dishPage,dishDtoPage,"records");

        List<Dish> dishList = dishPage.getRecords();
        List<DishDto> dishDtoList = new ArrayList<>();

        for (Dish dish : dishList) {
            //1.创建DishDto对象  将dish对象中数据都填充到DishDto对象中
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish,dishDto);  //这一步走完  相当于将菜品信息封装到了DishDto对象中
            //2.将菜品所属分类名称封装到DishDto对象中
            Category category = categoryService.getById(dish.getCategoryId());
            dishDto.setCategoryName(category.getName());
            //3.将每一个封装好的DishDto对象存入到List<DishDto>集合中
            dishDtoList.add(dishDto);
        }

        //优化步骤3：需要将当前页数据dishDtoList设置到dishDtoPage的records属性上
        dishDtoPage.setRecords(dishDtoList);

        //4.封装分页数据返回   此时返回的是dishDtoPage
        return  R.success(dishDtoPage);
    }


    @GetMapping("/{id}")
    public R<DishDto> getByIdWithFlavor(@PathVariable Long id){
        //需要根据id查询出菜品信息及其口味信息 封装到DishDto中返回
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    @PutMapping
    public R update(@RequestBody DishDto dishDto){

        dishService.updateWithFlavor(dishDto);

        return R.success("菜品信息修改成功！");
    }

    /**
     * 根据菜品分类id categoryId或 菜品名称name 查询菜品列表信息
     * @param dish 如果参数有categoryId或name 就封装到Dish对象中接收请求参数
     * @return 菜品信息列表
     */
    @GetMapping("/list")
    public R<List<Dish>> getListByCondition(Dish dish){
        //1.设置查询条件
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();

        //1.1:如果categoryId不为null 根据菜品分类id查询；
        dishLambdaQueryWrapper.eq(dish.getCategoryId()!=null,Dish::getCategoryId,dish.getCategoryId());

        //1.2:如果name不为null 根据菜品名称分类查询

        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(dish.getName()),Dish::getName,dish.getName());
        //执行查询操作
        List<Dish> list = dishService.list(dishLambdaQueryWrapper);
        return R.success(list);
    }


























}
