package com.huixu.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.huixu.bean.Employee;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author XuHui
 * @version 1.0
 */
@Mapper
public interface EmployeeDao extends BaseMapper<Employee>{
}
