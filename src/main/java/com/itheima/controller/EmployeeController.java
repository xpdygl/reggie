package com.itheima.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.bean.Dish;
import com.itheima.bean.Employee;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import com.itheima.service.DishService;
import com.itheima.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author XuHui
 * @version 1.0
 */
@Slf4j
@RequestMapping("/employee")
@RestController
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private DishService dishService;

      /*
        处理员工登录
            参数：接收到用户名和密码 Employee  登录成功 将id存入session
            返回值：
                登录成功：返回员工信息
                登录失败：返回就是错误信息
                使用R结果类封装数据返回给前端
     */
    //@PostMapping("/login")

    @PostMapping("/login")
    public R<Employee> login(@RequestBody Employee employee, HttpSession session){
        //1.对员工密码进行MD5加密
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据员工的账号查询员工信息 得到员工对象
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Employee::getUsername,employee.getUsername());
        //emp：根据员工账号从数据库中查询得到的Employee对象
        Employee emp = employeeService.getOne(lambdaQueryWrapper);



        //3.判断员工对象是否为null 如果是就登录失败
        if (emp==null){
            return R.error("员工不存在");
        }

        //4.判断员工登录输入的密码和数据库密码是否一致 如果不一致 登录失败
        if (!emp.getPassword().equals(password)){
            return R.error("员工账号密码不匹配！");

        }
        //5.判断员工账号是否为禁用 如果status为0 表示已经禁用
        if (emp.getStatus()==0){
            return R.error("员工已禁用！");
        }
        //6.将登录员工的ID存入session 响应登录成功 当该员工登录后进行一系列操作时 都要记录是谁操作的
        session.setAttribute("employee",emp.getId());
        return R.success(emp);
    }


    @PostMapping("/loginout")
    public R<String> logout(HttpSession session){
        //1.清理session中储存的员工ID
        session.removeAttribute("employee");
        //2.返回结果
        return  R.success("退出成功");
    }

    //employee + post
    /**
     * 新增员工
     * @param employee  要新增的员工信息
     * @param session   用于获取当前操作后台管理系统的员工id
     * @return 新增成功
     */
    @PostMapping
    public R<String> save(@RequestBody Employee employee,HttpSession session){
        //1.补全Employee字段信息
        //1.1：设置新增员工的初始密码 123456 要使用MD5加密
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
        //1.2设置员工的状态 数据库已经默认为1了 就可以不用设置了
        //employee.setStatus(1);
        //1.2：设置创建时间和修改时间
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        //1.4设置创建人和修改人id
//        Long empId = (Long) session.getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);


        //2.调用SERVICE完成新增
        boolean flag = employeeService.save(employee);
        if (flag){
            return  R.success("新增员工成功！");
        }else {
            return R.error("新增员工失败！");
        }
    }


    //员工分页查询  /employee/page + get
    @GetMapping("/page")
    public R<Page<Employee>> page(int page,int pageSize,String name){
        //1.创建page对象 设置分页查询条件【当前页码 每页显示条数】
        Page<Employee> pageInfo = new Page<>(page, pageSize);

        //2.创建LambdaQueryWrapper对象 设置分页查询条件【name  根据员工姓名模糊查询】
        LambdaQueryWrapper<Employee> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);

        //3.执行分页查询  返回分页查询数据
        employeeService.page(pageInfo,lambdaQueryWrapper);
        return R.success(pageInfo);
    }


    //员工的启用禁用  员工信息编辑
    // /employee + put

    /**
     * 员工信息编辑
     * @param employee 要修改的员工信息[根据id修改status]
     * @param session 用于获取当前登录后台管理系统的管理员id
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody Employee employee,HttpSession session){
        //1.获取登录管理员的员工id
        Long empId = (Long) session.getAttribute("employee");
        //2.修改员工信息  设置修改时间update_time 和 修改人 update_user
        employee.setUpdateUser(empId);
        employee.setUpdateTime(LocalDateTime.now());


        employeeService.updateById(employee);
        return R.success("员工修改成功");
    }


    //根据id查询员工信息
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){

        Employee byId = employeeService.getById(id);

        if (byId!=null){
            return R.success(byId);
        }
        return R.error("没有查到员工信息！");
    }


}
