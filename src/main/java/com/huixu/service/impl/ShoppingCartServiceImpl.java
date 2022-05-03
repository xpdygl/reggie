package com.huixu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huixu.bean.ShoppingCart;
import com.huixu.dao.ShoppingCartDao;
import com.huixu.service.ShoppingCartService;
import org.springframework.stereotype.Service;

/**
 * @author XuHui
 * @version 1.0
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartDao, ShoppingCart> implements ShoppingCartService {
}
