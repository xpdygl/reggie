package com.huixu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huixu.bean.DishFlavor;
import com.huixu.dao.DishFlavorDao;
import com.huixu.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorDao, DishFlavor> implements DishFlavorService {
}
