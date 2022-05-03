package com.huixu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huixu.bean.Dish;
import com.huixu.bean.DishFlavor;
import com.huixu.dao.DishDao;
import com.huixu.dto.DishDto;
import com.huixu.service.DishFlavorService;
import com.huixu.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishDao, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private DishService dishService;

    //由于在当前业务方法进行了多个数据库操作  需要菜品信息和口味信息都保存成功 才算添加菜品成功  此时就需要添加事务管理
    /*
        1.在当前方法上打上@Transactional  表示使用事务
        2.在项目启动类上打上@EnableTransactionManagement注解  开启事务支持
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //1.保存菜品信息到dish表
        this.save(dishDto);

        //2.保存菜品口味信息到dish_flavor表
        //2.1：获取菜品口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();

        //获取当前菜品id  并设置口味信息对应的菜品id
        Long dishId = dishDto.getId();

        //方式一 ：for循环遍历 设置菜品口味对应的菜品id
        /*for (DishFlavor flavor : flavors) {
            flavor.setDishId(dishId);
        }*/

        //方式二：JDK8 Stream流处理
        flavors = flavors.stream().map((flavor)->{
            flavor.setDishId(dishId);
            return flavor;
        }).collect(Collectors.toList());

        //2.2：保存菜品口味信息
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //0.创建DishDto对象
        DishDto dishDto = new DishDto();

        //1.根据菜品id查询菜品信息  封装到DishDto对象中
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish,dishDto);

        //2.根据菜品id查询对应的口味列表信息  封装到DishDto对象中
        //2.1:设置查询条件 因为在口味表存在一个字段dish_id对应菜品id
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(dishFlavorLambdaQueryWrapper);
        dishDto.setFlavors(list);

        //3.返回DishDto对象
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //1.修改菜品的基本信息
        this.updateById(dishDto);

        //2.修改菜品的口味信息
        //获取菜品id
        Long dishId = dishDto.getId();

        //2.1：删除该菜品原有的口味信息
        LambdaQueryWrapper<DishFlavor> dishFlavorLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishFlavorLambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
        dishFlavorService.remove(dishFlavorLambdaQueryWrapper);

        //2.2：新增该菜品的当前口味信息
        List<DishFlavor> flavors = dishDto.getFlavors();
        for (DishFlavor flavor : flavors) {
            //因为可能还存在之前的数据  所以直接把之前的id设置为null  放在在新增是出现id冲突
            flavor.setId(null);
            flavor.setDishId(dishId);
        }
        dishFlavorService.saveBatch(flavors);

    }

//    @Override
//    public void remove(List<Dish> ids) {
//
//        LambdaQueryWrapper<Dish> wrapper = new LambdaQueryWrapper<>();
//        wrapper.in(Dish::getId,ids);
//        //获得status信息
//        Dish service = dishService.getById(wrapper);
//        Integer status = service.getStatus();
//
//        //判断status是1还是0
//        if(status==1){
//            //说明该分类下有菜品 不能删除分类 抛出一个自定义的异常
//            throw new CustomException("该菜品在售，不能删除！");
//        }
//
//        //如果菜品没有在售就可以删除这个菜品
//        super.removeByIds(ids);
//    }
}
