package com.huixu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixu.bean.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDao extends BaseMapper<Orders> {
}
