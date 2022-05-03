package com.huixu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.huixu.bean.AddressBook;
import com.huixu.dao.AddressBookDao;
import com.huixu.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author XuHui
 * @version 1.0
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookDao, AddressBook> implements AddressBookService{
}
