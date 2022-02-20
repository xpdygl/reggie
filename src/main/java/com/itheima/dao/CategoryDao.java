package com.itheima.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.itheima.bean.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author XuHui
 * @version 1.0
 */
@Mapper
public interface CategoryDao extends BaseMapper<Category> {
}
