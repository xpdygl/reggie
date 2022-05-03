package com.huixu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixu.bean.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderDetailDao extends BaseMapper<OrderDetail> {
}
