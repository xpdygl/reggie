package com.huixu.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.huixu.bean.User;
import com.huixu.common.R;
import com.huixu.service.UserService;
import com.huixu.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @PostMapping("/sendMsg")
    public R sendMsg(@RequestBody User user, HttpSession session) {
        //1.获取手机号
        String phone = user.getPhone();
        //2.使用工具类生成验证码
        Integer validateCode = ValidateCodeUtils.generateValidateCode(4);
        //3.使用短信发送工具类 发送验证码给用户
        //SMSUtils.sendMessage("瑞吉外卖","瑞吉",phone,validateCode.toString());
        log.info("验证码：{}", validateCode);

        //4.为了方便和前台用户输入的验证码进行比对 所以要将生成的验证码存在session中  【数据库 Redis】
        //存手机验证码到session中时  应该使用电话号作为key
        //session.setAttribute(phone, validateCode);

        /*================2 将值存入redis================*/
        redisTemplate.opsForValue().set(phone,validateCode.toString(),5, TimeUnit.MINUTES);

        //5.响应 发送验证码成功
        return R.success("验证码发送成功！");
    }

    //可以使用Map集合接收json格式参数
    @PostMapping("/login")
    public R login(@RequestBody Map map, HttpSession session) {
        log.info("map:{}", map);
        //1.获取用户输入的手机号和验证码
        String phone = (String) map.get("phone");
        String code = (String) map.get("code");

        //2.获取session中存储的验证码
        //Object validateCode = session.getAttribute(phone);

        /*================3.1 将值从redis拿出================*/

        Object validateCode = redisTemplate.opsForValue().get(phone);


        //3.比对用户输入的验证码和session中存储的验证码是否一致  一致登录成功
        if (validateCode != null && code.equals(validateCode.toString())) {
            //4.一致登录成功后
            // 4.1：根据手机号去user表中查询是否存在用户user
            LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(User::getPhone, phone);
            User user = userService.getOne(wrapper);

            // 4.2：如果不存在，则添加到user表中
            if (user == null) {
                user = new User();
                user.setPhone(phone);
                userService.save(user);
            }

            //4.3：登录成功  将user的id存入到session中
            session.setAttribute("user", user.getId());

            /*================3.2 登录成功，将Redis的值删掉================*/

            redisTemplate.delete(phone);

            return R.success(user);
        }

        //5.不一致 登录失败
        return R.success("登录失败！");
    }

    /**
     * Request URL: http://localhost/user/loginout
     * Request Method: POST
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {


        session.removeAttribute("user");
        return R.success("退出成功");

    }
}