# 项目介绍

这是一个web在线点餐项目 也可以打包成微信小程序，项目具体的功能如下。



+ ### 前台模块

+ 登录注册
+ 个人信息
+ 地址簿
+ 购物车
+ 查看菜品
+ 支付系统



+ ### 后端模块

后端有两个角色，一个老板，一个是员工，老板可以通过员工管理模块管理员工

然后是五个模块

+ 员工管理
+ 分类管理
+ 菜品管理
+ 套餐管理
+ 订单明细


**这里有一个逻辑，当套餐启售时，他包含的菜品不能停售**

**这里有一个逻辑，当套餐/菜品启售时，他包含的套餐/菜品不能停售**

**新建菜品和新建套餐时有一个上传图片的功能**




### 技术选型

```
开发系统：MacOS 10.14.6 
开发语言：Java
开发编辑器：IntelliJ IDEA 2020.1.3
JDk版本：1.8
数据库及GUI工具：MySQL 8.0.20，Redis 3.2.8
数据库连接池：Druid连接池
Web容器：Tomcat 8
数据交换载体：JSON
后端框架：SpringBoot，MyBatisPlus
测试框架：Junit4
项目管理工具：Maven
```



### 项目结构

我起的包名都较为规范，英文翻译成中文就知道每个包里面放的是什么了。

### 启动项目

+ 打开代码
+ 连接mysql数据库，账号密码都是root，检查数据库reggie是否存在
+ 连接redis步骤如下
  + cd /usr/local
  + cd redis/redis-3.2.8/src/
  + ./redis-server
  + 打开redis本地客户端，能连上就算成功
+ 然后运行瑞吉项目
+ 运行成功后
+ 打开网址http://localhost/backend/page/login/login.html（后端登录入口
+ 打开网址http://localhost/front/page/login.html （前端登录入口



### 系统亮点

1. 全系统全程采用了AJAX异步请求操作，摒弃了同步请求中用户等待而无法进行操作的问题，提升了用户的体验。前后端之间的数据传递采用流行的JSON格式，数据包精简且高效。在后端的Controller接取数据方面，我使用了@RestController注解，该注解能够使控制器无刷新回写数据，配合前后端缩短系统响应的等待时间，让用户体验更佳。
2.  摒弃JSP，实现前后端技术分离 ：传统JSP项目前后端技术代码杂糅问题较为严重，宿递系统的解决方案是使用基于ElementUI提供的模板引擎插件，配合Ajax以及JSON进行数据的传递及显示。
3. 对系统加了缓存优化，使用Redis进行缓存，分担了MySQL的计算压力，用户查看菜品时只要菜品信息不改变就不用再用MySQL运算，而是直接读redis中的缓存。
4. 验证码有过期时间，验证成功后会将验证码删除保证系统安全性。





### 使用说明

将项目部署后运行数据库文件（db_reggie.sql）
我是mac和windows的磁盘路径不一样，由于项目未部署于云服务器中，仅运行在本地Tomcat服务器，所以你们的图片上传地址需要自己创建文件夹并加入图片。

修改application.yml里的upload-path为自己本地文件的地址就行了（不要有中文路径

```
reggie:
  upload-path: /Users/xuhui/Desktop/upload/
```


