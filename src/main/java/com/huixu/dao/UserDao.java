package com.huixu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixu.bean.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserDao extends BaseMapper<User> {
}
