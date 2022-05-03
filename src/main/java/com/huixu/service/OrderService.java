package com.huixu.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.huixu.bean.Orders;

public interface OrderService extends IService<Orders> {

    void submit(Orders orders);
}
