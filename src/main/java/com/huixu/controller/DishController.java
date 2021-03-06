package com.huixu.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huixu.bean.Category;
import com.huixu.bean.Dish;
import com.huixu.bean.DishFlavor;
import com.huixu.common.R;
import com.huixu.dto.DishDto;
import com.huixu.service.CategoryService;
import com.huixu.service.DishFlavorService;
import com.huixu.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @Autowired
    private DishFlavorService dishFlavorService;

    /*=====================1.注入RedisTemplate对象========================*/
    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping
    public R save(@RequestBody DishDto dishDto) {
        log.info("dish:{}", dishDto);
        dishService.saveWithFlavor(dishDto);
        /*=====================2.后台添加菜品成功，需要删除redis中该菜品分类的缓存数据========================*/
        String key =  "dish_"+dishDto.getCategoryName()+"_"+dishDto.getStatus();
        redisTemplate.delete(key);
        return R.success("菜品增加成功！");

    }


    @GetMapping("/page")
    public R page(Integer page, Integer pageSize, String name) {
        //0.配置MyBatisPlus分页拦截器【已完成】
        //1.设置分页条件 【当前页码和每页显示条数】  使用Page对象设置
        Page<Dish> dishPage = new Page<>(page, pageSize);
        //优化步骤1：返回的数据应该封装到Page<DishDto>中
        Page<DishDto> dishDtoPage = new Page<DishDto>();

        //2.设置分页查询条件 【菜品名称name 模糊查询 有就设置  没有就不设置】
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Dish::getName, name);

        //3.执行分页查询  查询之后会将total、records重新填入到Page对象中
        dishService.page(dishPage, dishLambdaQueryWrapper);

        //优化步骤2：需要将查询出来的菜品分页信息数据dishPage封装到dishDtoPage中   dishDtoPage=dishPage+categoryName
        //因为dishPage.getRecords得到的是List<Dish>当前页数据  在数据中没有菜品分类名称  需要得到当前菜品信息 List<Dish>添加上分类名称后存入到List<DishDto>
        BeanUtils.copyProperties(dishPage, dishDtoPage, "records");

        List<Dish> dishList = dishPage.getRecords();
        List<DishDto> dishDtoList = new ArrayList<>();

        for (Dish dish : dishList) {
            //1.创建DishDto对象  将dish对象中数据都填充到DishDto对象中
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(dish, dishDto);  //这一步走完  相当于将菜品信息封装到了DishDto对象中
            //2.将菜品所属分类名称封装到DishDto对象中
            Category category = categoryService.getById(dish.getCategoryId());
            dishDto.setCategoryName(category.getName());
            //3.将每一个封装好的DishDto对象存入到List<DishDto>集合中
            dishDtoList.add(dishDto);
        }

        //优化步骤3：需要将当前页数据dishDtoList设置到dishDtoPage的records属性上
        dishDtoPage.setRecords(dishDtoList);

        //4.封装分页数据返回   此时返回的是dishDtoPage
        return R.success(dishDtoPage);
    }


    @GetMapping("/{id}")
    public R<DishDto> getByIdWithFlavor(@PathVariable Long id) {
        //需要根据id查询出菜品信息及其口味信息 封装到DishDto中返回
        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }


    @PutMapping
    public R update(@RequestBody DishDto dishDto) {
        //在菜品信息修改之前，获取该菜品原来的分类id
        Long dishId = dishDto.getId();
        Long oldCategoryId = dishService.getById(dishId).getCategoryId();
        Long newCategoryId = dishDto.getCategoryId();

        dishService.updateWithFlavor(dishDto);

        /*=====================优化：2.菜品信息修改成功后，删除redis中的所有菜品分类缓存数据========================*/
        //该菜品原来所属的分类缓存数据key
        //删除原有的分类对象的缓存数据
        //该菜品现在所属的分类缓存数据key
        //删除新分类对应的缓存数据
        String key = (String) "dish_"+oldCategoryId+"_"+dishDto.getStatus();
        redisTemplate.delete(key);
        if (!oldCategoryId.equals(newCategoryId)){
            key = (String) "dish_"+newCategoryId+"_"+dishDto.getStatus();
            System.out.println("换了新的redis缓存");
            redisTemplate.delete(key);
        }
        return R.success("菜品信息修改成功！");
    }


    /*=====================优化：将菜品分类下的列表数据 存入到redis缓存中========================*/
    /**
     * 根据菜品分类id categoryId或 菜品名称name 查询菜品列表信息
     *
     * @param dish 如果参数有categoryId或name 就封装到Dish对象中接收请求参数
     * @return 菜品信息列表
     */
    @GetMapping("/list")
    public R<List<DishDto>> getListByCondition(Dish dish) {

        /*=====================2.1：判断redis缓存中是否有该菜品分类数据========================*/
        //如果有  则从redis中获取   如果没有 则需要查询数据库，查询完成将数据存入到redis一份
        String key = "dish_"+dish.getCategoryId()+"_"+dish.getStatus();
        String dishDTOListStr = (String) redisTemplate.opsForValue().get(key);

        if (dishDTOListStr!=null){
            //从redis中获取数据返回
            System.out.println("从redis缓存中查询菜品分类列表数据...");
            //将redis缓存中获取的json字符串数据重新转为list集合
            return R.success(JSON.parseObject(dishDTOListStr,List.class));
        }

        //走到这里 说明redis中没有缓存。
        System.out.println("从MySQL数据库中查询菜品分类列表数据...");

        //1.设置查询条件
        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();

        //1.1:如果categoryId不为null 根据菜品分类id查询；
        wrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId, dish.getCategoryId());
        //1.2:如果name不为null 根据菜品名称分类查询
        wrapper.like(StringUtils.isNotEmpty(dish.getName()), Dish::getName, dish.getName());
        //1.3:如果状态不为null 根据菜品状态查询  查询在售：status=1
        wrapper.eq(dish.getStatus() != null, Dish::getStatus, dish.getStatus());

        //2.执行查询操作
        List<Dish> list = dishService.list(wrapper);

        //3.将菜品信息及其口味信息以及所属菜品分类名称 都封装到DishDto对象中
        List<DishDto> dishDtoList = new ArrayList<>();

        //由于当前是根据菜品分类id查询其下面的菜品列表信息  所以此时所有的菜品都属于同一分类 因此就可以将获取菜品分类名称提取出来
        Category category = categoryService.getById(dish.getCategoryId());
        String categoryName = category.getName();

        for (Dish dish1 : list) {
            //3.0：准备 创建一个DishDto对象
            DishDto dishDto = new DishDto();
            //3.1：封装菜品基本信息dish1到DishDto中
            BeanUtils.copyProperties(dish1, dishDto);
            //3.2：封装菜品分类名称到DishDto中
            dishDto.setCategoryName(categoryName);
            //3.3：封装菜品口味信息到DishDto中
            //3.3.1:获取菜品id
            Long dishId = dish1.getId();
            //3.3.2：根据菜品id去菜品口味表中查询其对应的口味信息
            LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
            dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId, dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);
            //3.4：将DishDto对象在添加到dishDtoList中
            dishDtoList.add(dishDto);
        }

        /*=====================2.2：走到这里 说明当前redis缓存中没有该菜品分类数据 是第一次从MySQL数据库中查询 因此需要将数据存入到Redis中一份=====================*/
        //redis中要存入的数据时当前菜品分类下的菜品列表数据  也就是dishDtoList

        redisTemplate.opsForValue().set(key, JSON.toJSONString(dishDtoList));
        return R.success(dishDtoList);
    }


    //起售停售|批量起售停售
    @PostMapping("/status/{status}")
    public R updateDishStatus(@PathVariable Integer status, @RequestParam List<Long> ids) {
        log.info("status:{}", status);
        log.info("ids:{}", ids);
        //status

        LambdaUpdateWrapper<Dish> wrapper = new LambdaUpdateWrapper<>();
        wrapper.in(Dish::getId, ids);
        //where id = wrapper
        wrapper.set(Dish::getStatus, status);
        dishService.update(wrapper);
        return R.success("成功");
    }

    /**
     * Request URL: http://localhost/dish?ids=1397849739276890114
     * Request Method: DELETE
     * Status Code: 405
     */

    @DeleteMapping
    public R<String> deleteById(@RequestParam List<Long> ids) {
        for (Long id : ids) {
            LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Dish::getId, id);
            Dish one = dishService.getOne(wrapper);
            one.setUpdateTime(LocalDateTime.now());
            dishService.update(one, wrapper);
            if (one.getStatus() == 1) {
                //说明该分类下有菜品 不能删除分类 抛出一个自定义的异常
                return R.error(one.getName()+"该菜品在售，不能删除！");
            }
        }

        dishService.removeByIds(ids);
        return R.success("菜品删除成功");

    }


}
