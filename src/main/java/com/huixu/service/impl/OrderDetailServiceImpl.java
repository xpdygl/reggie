package com.huixu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huixu.bean.OrderDetail;
import com.huixu.dao.OrderDetailDao;
import com.huixu.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailDao, OrderDetail>implements OrderDetailService {
}
