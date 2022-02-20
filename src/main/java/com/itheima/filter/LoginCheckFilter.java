package com.itheima.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/*
    登录检查过滤器实现步骤：
        1.创建一个过滤器，配置过滤路径 /*
        2.在SpringBoot项目启动类上加上@ServletComponentScan 注解
        3.编写过滤器处理业务逻辑代码
 */
@Slf4j
@WebFilter("/*")
public class LoginCheckFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        System.out.println("进来了....");
        //0.强转两个对象
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //1.获取本次请求的URI
        String uri = request.getRequestURI();
        System.out.println("本次请求：uri = " + uri);

        //2.判断本次请求是否需要过滤处理
        //传统方式实现：
        /*if(uri.contains("login") || uri.contains("logout")||uri.contains(".js")||uri.contains(".css")
           ||uri.contains("images") ||uri.contains("plugins")||uri.contains(".ico")){
            //3.如果不需要过滤处理 直接放行
            filterChain.doFilter(request,response);
            return; //跳出方法 后面的代码不要执行了
        }*/

        /*
            新的方式实现：判断某些地址不需要进行过滤处理
                //1.定义不需要进行过滤处理的请求路径
                //2.使用路径匹配器 来去匹配请求地址是否属于不需要处理的路径

         */
        String[] urls = {"/employee/login","/employee/logout","/backend/**","/front/**","/user/sendMsg","/user/login","/user/loginout"};
        AntPathMatcher pathMatcher = new AntPathMatcher();
        boolean flag = false;  //定义标识  表示当前请求地址是否需要过滤处理  如果不需要为true  需要为false
        for (String url : urls) {
            //url：不需要进行过滤处理的路径规则   uri：本次请求路径地址
            if(pathMatcher.match(url,uri)){
                flag = true;
                break;
            }
        }

        if(flag){
            //3.如果不需要过滤处理 直接放行
            filterChain.doFilter(request,response);
            return; //跳出方法 后面的代码不要执行了
        }



        //4.如果需要处理 判断员工是否已经登录
        Object employee = request.getSession().getAttribute("employee");
        //5.员工登录了 就放行 正常访问
        if(employee!=null){
            Long empId = (Long) request.getSession().getAttribute("employee");
            //登录成功后  经过过滤器时  将当前登录的员工id存入到ThreadLocal中
            BaseContext.set(empId);
            filterChain.doFilter(request,response);
            return; //跳出方法 后面的代码不要执行了
        }

        //判断用户是否登录  用户登录了就直接放行
        Object user = request.getSession().getAttribute("user");
        if(user!=null){
            BaseContext.set((Long)user);

            filterChain.doFilter(request,response);
            return; //跳出方法 后面的代码不要执行了
        }

        //6.没有登录 就返回未登录 跳转到登录页面
        response.getWriter().print(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    @Override
    public void destroy() {

    }
}
