package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.bean.Category;
import com.itheima.common.R;
import com.itheima.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author XuHui
 * @version 1.0
 */

@RequestMapping("/category")
@RestController
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    //新增分类
    @PostMapping
    public R<String> save(@RequestBody Category category){
        categoryService.save(category);
        return R.success("增加分类成功！");
    }


//@RestController
//@RequestMapping("/category")
//public class CategoryController {
//    @Autowired
//    private CategoryService categoryService;
//
//    //新增分类
//    @PostMapping
//    public R<String> save(@RequestBody Category category){
//        categoryService.save(category);
//        return R.success("增加分类成功");
//    }
    /*
        MyBatisPlus分页查询实现：
            1.配置分页查询拦截器   【前面已经配置好了】
            2.创建Page对象    封装查询条件【当前页码：page  每页显示条数：pageSize】
              设置分页查询条件【eg：员工分页查询是  根据姓名name进行模糊查询】
            3.调用分页查询方法  会将分页所需的数据重新回填到Page对象中
     */

    //分页查询

    @GetMapping("/page")
    public R<Page> findPage(int page,int pageSize){

        //1.创建Page对象 设置分页条件
        Page<Category> pageInfo = new Page<>(page, pageSize);
        //2.设置分页查询条件规则  用QueryWrapper或LambdaQueryWrapper对象
        LambdaQueryWrapper<Category> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.orderByAsc(Category::getSort);
        //3.调用分页查询
        categoryService.page(pageInfo,lambdaQueryWrapper);
        //4.将分页查询数据封装到R对象中进行返回给前端
        return R.success(pageInfo);
    }

    /*删除分类*/
    @DeleteMapping("/{id}")
    public R<String> delete(@PathVariable Long id){

        //调用自定义扩展的根据id删除分类方法
        //因为在这个方法中我们删除分类时会检查该分类下是否存在菜品和套餐
        categoryService.remove(id);
        return R.success("分类删除成功");
    }


    //修改分类
    @PutMapping
    public R<String> update(@RequestBody Category category){
        categoryService.updateById(category);
        return R.success("修改成功");
    }

    @GetMapping("/list")
    public R<List<Category>> getByType(Integer type){
        //1.设置查询条件

        LambdaQueryWrapper<Category> categoryLambdaQueryWrapper = new LambdaQueryWrapper<>();
        categoryLambdaQueryWrapper.eq(type!=null,Category::getType,type);

        //执行查询
        List<Category> list = categoryService.list(categoryLambdaQueryWrapper);

        //3.封装查询结果返回
        return R.success(list);
    }


}
