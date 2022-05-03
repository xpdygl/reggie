package com.huixu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huixu.bean.Employee;
import com.huixu.dao.EmployeeDao;
import com.huixu.service.EmployeeService;
import org.springframework.stereotype.Service;

/**
 * @author XuHui
 * @version 1.0
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeDao, Employee> implements EmployeeService {
}
