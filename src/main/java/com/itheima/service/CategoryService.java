package com.itheima.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.bean.Category;

/**
 * @author XuHui
 * @version 1.0
 */
public interface CategoryService extends IService<Category> {

    /**
     * 自定义扩展的根据id删除分类
     * @param id
     */
    void  remove (Long id);
}
