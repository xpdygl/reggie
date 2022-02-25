package com.itheima.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.bean.*;
import com.itheima.common.BaseContext;
import com.itheima.dao.OrderDao;
import com.itheima.exception.CustomException;
import com.itheima.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class OrderServiceImpl extends ServiceImpl<OrderDao, Orders> implements OrderService {

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private OrderDetailService orderDetailService;

    @Override
    @Transactional
    public void submit(Orders orders) {

        //1.获取当前登录的用户id  得到当前登录的用户信息
        Long userId = BaseContext.get();
        User user = userService.getById(userId);

        //2.获取当前用户填写的地址信息   【万一没填写 或者提交的数据被篡改 不能下单】
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if(addressBook==null){
            throw  new CustomException("用户地址为空 不能下单！");
        }

        //3.根据当前登录用户获取购物车数据  【购物车数据为null 则不能下单】
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        if(shoppingCartList==null || shoppingCartList.size()==0){
            throw  new CustomException("购物车为空 不能下单！");
        }

        //手动生成orders表的id主键
        long orderId = IdWorker.getId();

        //4.向order_detail包中保存订单详情信息  实际上做的就是将原来该用户购物车中的商品信息转移到 订单详情表中
        int sumAmount = 0;

        //4.1:设置订单详情信息
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for (ShoppingCart shoppingCart : shoppingCartList) {
            OrderDetail orderDetail = new OrderDetail();
            BeanUtils.copyProperties(shoppingCart,orderDetail);
            //1.设置订单id
            orderDetail.setOrderId(orderId);
            //2.计算出总金额
            sumAmount += (orderDetail.getAmount().multiply(new BigDecimal(orderDetail.getNumber()))).intValue();

            orderDetailList.add(orderDetail);
        }
        //4.2：保存订单详情信息
        orderDetailService.saveBatch(orderDetailList);


        //5.向orders表中保存订单的基本信息
        //5.1：补全订单基本信息
        //1.orders表的id主键不需要设置  数据库会自动生成【用已经手动生成的】
        orders.setId(orderId);
        //2.number：订单号  手动生成 可以使用UUID也可以使用雪花算法
        String orderNumber = UUID.randomUUID().toString().replace("-", "");
        orders.setNumber(orderNumber);
        //3.status:订单状态
        orders.setStatus(1);
        //3.user_id：当前下单用户
        orders.setUserId(userId);
        //4.address_book_id：无需设置 前端已经传递过来了
        //5.order_time：下单时间
        orders.setOrderTime(LocalDateTime.now());
        //6.checkout_time：支付时间
        orders.setCheckoutTime(LocalDateTime.now());
        //7.pay_method：支付方式  无需设置 前端已经传递过来了 1：微信支付  2：支付宝
        //8.amount：总金额 金额必须后端根据商品单价和商品数量重新计算
        orders.setAmount(new BigDecimal(sumAmount));
        //9.phone：收货人电话  addressBook.getPhone()
        orders.setPhone(addressBook.getPhone());
        //10.address:收货人地址  应该从addressBook对象中获取数据 由province+city+district+detail拼接起来 得到具体收货地址
        orders.setAddress(
                (addressBook.getProvinceName()==null?"":addressBook.getProvinceName())+
                        (addressBook.getCityName()==null?"":addressBook.getCityName())+
                        (addressBook.getDistrictName()==null?"":addressBook.getDistrictName())+
                        (addressBook.getDetail()==null?"":addressBook.getDetail())
        );
        //11.user_name：下单人姓名   user.getName
        orders.setUserName(user.getName());
        //12.consignee：收货人姓名   addressBook.getConsignee
        orders.setConsignee(addressBook.getConsignee());

        //5.2：保存订单基本信息到订单表orders
        this.save(orders);

        //6.清空购物车
        shoppingCartService.remove(shoppingCartLambdaQueryWrapper);
    }
}
