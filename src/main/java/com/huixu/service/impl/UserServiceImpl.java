package com.huixu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huixu.bean.User;
import com.huixu.dao.UserDao;
import com.huixu.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserDao, User> implements UserService {
}
