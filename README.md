**基于SpringBoot实现Java高并发之秒杀系统**

**技术栈**

* 后端： SpringBoot-2.x + Redis-5.x

* 前端： Bootstrap + Jquery

**测试环境**

* IDEA + Maven + Tomcat + JDK8

**启动说明**
* 启动前，请创建数据库`seckill`,和 SSM 版本建表相同
* 启动前，请配置好 [application.yml](https://github.com/Acc2020/seckill/blob/seckill_sboot/src/main/resources/application.yaml) 中连接数据库的用户名和密码，以及Redis服务器的地址和端口信息。
* 配置完成后，运行位于 `src/main/com/vsnode/`下的SpringbootSeckillApplication中的main方法，访问 `http://localhost:8080/seckill/` 进行API测试。
* 注意数据库中秒杀商品的日期可能要修改，自行修改为符合商品秒杀条件的时间即可。


**项目设计**
建包模式参考阿里 Java 开发手册
```
.
 ├─.mvn
 │  └─wrapper
 └─src
     ├─main
     │  ├─java
     │  │  └─com
     │  │      └─vsnode
     │  │          ├─config     -- 配置文件，配置后注入
     │  │          ├─constant   -- 静态变量
     │  │          ├─controller -- 控制层
     │  │          ├─dao        -- dao 层
     │  │          │  └─cache   -- 配置缓存Redis
     │  │          ├─exception  -- 异常
     │  │          ├─mapper     -- Mybatis-Mapper层映射接口，或称为DAO层
     │  │          ├─pojo       -- 简单实体类汇总
     │  │          │  ├─dto     -- 统一封装的一些结果属性，和entity类似
     │  │          │  ├─entity  -- 和数据库一一对应相当于 DO
     │  │          │  └─vo      -- 用户访问数据时结果集
     │  │          ├─service    -- 服务层
     │  │          │  └─impl    -- 业务层对应实现类，主要写逻辑
     │  │          └─util       -- 工具类汇总
     │  └─resources
     │      ├── application.yml     -- SpringBoot核心配置
     │      ├─mybatis               -- 存放 mybatis 配置
     │      │  └─mapper             -- Mybatis-Mapper层XML映射文件
     │      ├─sql                   -- 存放数据库文件
     │      ├─static                -- 存放静态资源 （这个项目的静态资源用的都是CDN）
     │      │  ├─lib
     │      │  └─script             -- 存放 js 代码  
     │      └─templates             -- 存放Thymeleaf模板引擎所需的HTML，不能在浏览器直接访问
     │          └─common            -- 公共文件
     └─test                   -- 测试文件
```
欢迎star(#^.^#)，可留言，可订阅微信公众号：调整算法和程序设计
问题交流可加QQ：807098855  