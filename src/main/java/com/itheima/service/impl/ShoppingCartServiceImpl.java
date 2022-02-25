package com.itheima.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.bean.ShoppingCart;
import com.itheima.dao.ShoppingCartDao;
import com.itheima.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author XuHui
 * @version 1.0
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartDao, ShoppingCart> implements ShoppingCartService {
}
