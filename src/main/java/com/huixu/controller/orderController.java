package com.huixu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.huixu.bean.Orders;
import com.huixu.common.R;
import com.huixu.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author XuHui
 * @version 1.0
 */
@RequestMapping("/order")
@Slf4j
@RestController
public class orderController {

    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public R submit(@RequestBody Orders orders) {
        log.info("orders:{}", orders);
        //调用service下单
        orderService.submit(orders);
        return R.success("用户下单成功");
    }


    /**
     * Request URL: http://localhost/order/page?page=1&pageSize=10
     * Request Method: GET
     * Status Code: 404
     */

    @GetMapping("/page")
    public R<Page<Orders>> getById(int page,int pageSize,String name){
        //1.创建page对象 设置分页查询条件【当前页码 每页显示条数】
        Page<Orders> ordersInfo = new Page<>(page, pageSize);
        //2.创建LambdaQueryWrapper对象 设置分页查询条件【name  根据员工姓名模糊查询】
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Orders::getUserName,name);

        //3.执行分页查询  返回分页查询数据
        orderService.page(ordersInfo,wrapper);

        return R.success(ordersInfo);
    }

    /**
     * Request URL: http://localhost/order/userPage?page=1&pageSize=5
     * Request Method: GET
     * Status Code: 404
     */

    @GetMapping("/userPage")
    public R<Page<Orders>> page(Integer page, Integer pageSize, String name){
        Page<Orders> ordersPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.isNotEmpty(name),Orders::getUserName,name);
        orderService.page(ordersPage,wrapper);
        return R.success(ordersPage);
    }

    /**
     * 修改订单状态
     * Request URL: http://localhost/order
     * Request Method: PUT
     * Status Code: 404 
     */
    
    @PutMapping
    public R update(@RequestBody Orders orders){
        log.info("进入订单");
        orderService.updateById(orders);
        return  R.success("更新订单状态成功");
    }

    /**
     * 再来一单
     * Request URL: http://localhost/order/again
     * Request Method: POST
     */

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){

        orderService.submit(orders);
        return   R.success("再次下单");

    }
}
