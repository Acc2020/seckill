## 秒杀项目基于SSM 和 SpringBoot2.2.x 版重构

**技术栈**

- 后端：
  - SSM版本：Spring + SpringMVC + MyBatis + Redis 点击：[seckill_ssm](https://github.com/Acc2020/seckill/tree/seckill_ssm)
  - SpringBoot版本： SpringBoot2.2.x + Redis 点击：[seckill_sboot](https://github.com/Acc2020/seckill/tree/seckill_sboot)
- 前端：Bootstrap + Jquery

或者点击 branch 切换分支

![1587478761485](http://images.vsnode.com/mynotes-images/202004/21/221925-161943.png)

**测试环境**

- IDEA + Maven- + Tomcat + JDK8

**启动说明**

- 启动前，请创建数据库`seckill`，建表SQL语句放在：[db/schema.sql](https://github.com/Acc2020/seckill/blob/master/web/resource/script/seckill.js)。具体的建表和建库语句请仔细看SQL文件。
- 不同的版本切换的不同的分支上查看配置说明

**说明**

SpringBoot不是对Spring功能上的增强，而是提供了一种快速使用Spring的方式，所以本质上和SSM框架差别不大，本人也是为了多练习使用，学习此项目不仅可以学习到秒杀系统的设计流程还能很好的练习一下SpringBoot框架。

**详细笔记查看**  
从环境搭建到优化笔记：[笔记](https://github.com/Acc2020/seckill/blob/master/%E7%A7%92%E6%9D%80%E9%A1%B9%E7%9B%AE.md)

**imooc 课程视频**

| 名称              | 地址                           |
| ----------------- | ------------------------------ |
| 业务分析与DAO实现 | http://www.imooc.com/learn/587 |
| Service层实现     | http://www.imooc.com/learn/631 |
| WEB层实现         | http://www.imooc.com/learn/630 |
| 并发优化          | http://www.imooc.com/learn/632 |


---

