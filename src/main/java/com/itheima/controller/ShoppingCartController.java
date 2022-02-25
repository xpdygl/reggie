package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.bean.Dish;
import com.itheima.bean.ShoppingCart;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author XuHui
 * @version 1.0
 */
@Slf4j
@RequestMapping("/shoppingCart")
@RestController
public class ShoppingCartController {


    @Autowired
    private ShoppingCartService shoppingCartService;

    //添加东西到购物车中，实际上就是更新表中的数据
    @PostMapping("/add")
    public R add(@RequestBody ShoppingCart shoppingCart){
        log.info("shoppingCart:{}",shoppingCart);
        //1.获取当前登录用户的id
        Long userId = BaseContext.get();

        //2.判断当前菜品或套餐是否已经添加到数据库了  根据name判断
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        //查询当前用户下的购物车数据
//        wrapper.eq(ShoppingCart::getUserId,shoppingCart.getUserId());
        //查询某个菜品或套餐是否存在
        wrapper.eq(ShoppingCart::getName,shoppingCart.getName());
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        //3.存在：number+1  不存在：设置number为1
        if (one != null){
            one.setNumber(one.getNumber()+1);
        }else {
            one = shoppingCart;
            one.setNumber(1);
        }


        //4.补全字段：设置购物车记录的用户id、设置当前系统时间
        one.setUserId(userId);
        one.setCreateTime(LocalDateTime.now());

        //5.插入菜品或套餐记录到数据库
        shoppingCartService.saveOrUpdate(one);
        return R.success("修改成功");
    }
    @GetMapping("/list")
    public R list(){
        //获得当前用户id
        Long userId = BaseContext.get();

        //设置查询条件
        LambdaQueryWrapper<ShoppingCart> Wrapper = new LambdaQueryWrapper<>();
        Wrapper.eq(userId!=null,ShoppingCart::getUserId,userId);
        //根据id查询购物车
        List<ShoppingCart> list = shoppingCartService.list(Wrapper);
        //返回数据
        return R.success(list);
    }

    @DeleteMapping("/clean")
    public R clean(){
        Long userId = BaseContext.get();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userId!=null,ShoppingCart::getUserId,userId);
        shoppingCartService.remove(wrapper);
        return R.success("删除成功");
    }

    @PostMapping("/sub")
    public R  delebyId(@RequestBody ShoppingCart shoppingCart){
        //1.获取当前登录用户的id
        Long userId = BaseContext.get();
        LambdaQueryWrapper<ShoppingCart> wrapper = new LambdaQueryWrapper<>();
        //判断是套餐还是菜品
        wrapper.eq(shoppingCart.getDishId()!=null,ShoppingCart::getDishId,shoppingCart.getDishId());
        wrapper.eq(shoppingCart.getSetmealId()!=null,ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        ShoppingCart one = shoppingCartService.getOne(wrapper);
        if (one!=null){
            shoppingCartService.removeById(one);
            return R.success("删除成功");

        }
        return R.error("删除失败");

//
//        wrapper.eq(ShoppingCart::getName,shoppingCart.getName());
//        ShoppingCart one = shoppingCartService.getOne(wrapper);
//        shoppingCartService.removeById(one);
    }

}
