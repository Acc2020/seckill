# 秒杀项目基于 SSM



## 相关技术
1. Spring&SpringMVC
2. MyBatis
3. MySQL
4. Restful
5. Bootstrap&jQuery

## 知识点
1. Mybatis使用与整合Spring技巧
2. SpringIOC与声明式事务理解
3. 业务接口封装技巧
4. SpringMVC和Restful理解和运用
5. 交互分析和JS模块化开发
6. 高并发秒杀系统瓶颈点分析
7. 事务,行级锁,网络延迟等理解
8. 前端,CDN,缓存理解

## 课程视频
| 名称              | 地址                           |
| ----------------- | ------------------------------ |
| 业务分析与DAO实现 | http://www.imooc.com/learn/587 |
| Service层实现     | http://www.imooc.com/learn/631 |
| WEB层实现         | http://www.imooc.com/learn/630 |
| 并发优化          | http://www.imooc.com/learn/632 |



---

> 以下是详细的过程，可用于复习，或者不看视频的使用，最后有自己遇见的报错的解决方案

## 1、创建项目

### 项目环境：

Java 版本 ： JDK 1.8

Maven 版本 ： maven 3.6.1

开发工具 ： IDEA





### 项目创建

项目使用 maven 创建方式，推荐使用 IDEA 直接创建 maven 项目

可以使用 maven 命令创建 web 骨架(maven 需要配置到环境变量)

```bash
mvn archetype:create -DgroupId=com.node.seckill -DartifactID=seckkill -DarchetypeArtifactId=maven-archetype-web
```

直接使用 IDEA 创建，添加 web 组件

### 导入依赖

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>org.example</groupId>
    <artifactId>seckill_ssm</artifactId>
    <version>1.0-SNAPSHOT</version>

    <dependencies>
        <!--使用 junit-->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13</version>
            <scope>test</scope>
        </dependency>
        <!--导入日志依赖-->
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>1.7.30</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-core</artifactId>
            <version>1.2.3</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.2.3</version>
        </dependency>
        <!--数据库依赖-->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>5.1.47</version>
        </dependency>
        <!--c3po -->
        <dependency>
            <groupId>c3p0</groupId>
            <artifactId>c3p0</artifactId>
            <version>0.9.1.2</version>
        </dependency>
        <!--Dao 层 mybatis-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis</artifactId>
            <version>3.5.4</version>
        </dependency>
        <!--mybatis-spring 整合-->
        <dependency>
            <groupId>org.mybatis</groupId>
            <artifactId>mybatis-spring</artifactId>
            <version>2.0.4</version>
        </dependency>
        <!--service： web 依赖-->
        <dependency>
            <groupId>taglibs</groupId>
            <artifactId>standard</artifactId>
            <version>1.1.2</version>
        </dependency>
        <dependency>
            <groupId>jstl</groupId>
            <artifactId>jstl</artifactId>
            <version>1.2</version>
        </dependency>
        <dependency>
            <groupId>com.fasterxml.jackson.core</groupId>
            <artifactId>jackson-databind</artifactId>
            <version>2.10.3</version>
        </dependency>
        <dependency>
            <groupId>javax.servlet</groupId>
            <artifactId>javax.servlet-api</artifactId>
            <version>4.0.1</version>
        </dependency>
        <!--spring 依赖-->
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-webmvc</artifactId>
            <version>5.2.5.RELEASE</version>
        </dependency>
        <dependency>
            <groupId>org.springframework</groupId>
            <artifactId>spring-jdbc</artifactId>
            <version>5.2.5.RELEASE</version>
        </dependency>
        <!--lombok 依赖-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>1.18.10</version>
            <scope>provided</scope>
        </dependency>
    </dependencies>

    <!--设置 Maven 过滤资源mysl-->
    <build>
        <resources>
            <resource>
                <directory>src/main/java</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
            <resource>
                <directory>src/main/resources</directory>
                <includes>
                    <include>**/*.properties</include>
                    <include>**/*.xml</include>
                </includes>
                <filtering>false</filtering>
            </resource>
        </resources>
    </build>
</project>
```

### 秒杀业务分析

秒杀业务主要是在商品增加调整库存，用户对库存进行购买秒杀的行为

![1586761908139](http://images.vsnode.com/mynotes-images/202004/13/151151-839212.png)

主要核心是对库存进行处理

![1586761949743](http://images.vsnode.com/mynotes-images/202004/13/151243-593531.png)

这里使用的是 MySQL（事务+行锁） ，现在 redis 也能很好的支持事务，尤其适合在高并发情况下完成。

## 2、数据库设计

```sql

-- 数据库初始话脚本

-- 创建数据库

CREATE DATABASE seckill;
-- 使用数据库
use seckill;

-- 创建秒杀库存表
CREATE TABLE seckill(
`seckill_id` bigint NOT NULL AUTO_INCREMENT COMMENT '商品库存id',
`name` varchar(120) NOT NULL COMMENT '商品名称',
`number` int NOT NULL COMMENT '库存数量',
`start_time` datetime NOT NULL COMMENT '秒杀开启时间',
`end_time` datetime NOT NULL COMMENT '秒杀关闭时间',
`create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
PRIMARY KEY (seckill_id),
KEY idx_start_time(start_time),
KEY idx_end_time(end_time),
KEY idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=utf8 COMMENT='秒杀库存表';

-- 初始化数据
INSERT INTO
    seckill (name, number, start_time, end_time)
values
    ('10000 元秒杀 MacBook Pro', 100, '2020-4-12 00:00:00', '2020-4-12 00:00:00'),
    ('1000 元秒杀 ipad Pro', 200, '2020-4-12 00:00:00', '2020-4-12 00:00:00'),
    ('1000 元秒杀 小米10', 300, '2020-4-12 00:00:00', '2020-4-12 00:00:00'),
    ('1000 元秒杀 红米 k30', 100, '2020-4-12 00:00:00', '2020-4-12 00:00:00');

-- 秒杀成功表
CREATE TABLE success_killed(
`seckill_id` bigint NOT NULL COMMENT '秒杀商品id',
`user_phone` bigint NOT NULL COMMENT '用户手机号',
`state` tinyint NOT NULL DEFAULT -1 COMMENT '状态标识位：-1 无效 0 成功 1 已付款',
`create_time` timestamp NOT NULL   COMMENT='创建时间',
PRIMARY KEY ("seckill_id","user_phone"),
KEY idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT ChARSET=utf8 COMMENT='秒杀明细表';
```

> 注意点

- 数据库使用时需要先创建 `数据库 seckill`, 数据库创建类型编码方式需要选择 utf8

## 3、整合 Dao 层

### mybatis 

使用 Mybatis 完成对 Dao 层数据映射（因为使用 mybatis dao 层包名可以为 mapper，效果一样）![1586762334189](http://images.vsnode.com/mynotes-images/202004/18/141054-129935.png)

3、配置对应的 xxxMapper.xml 配置文件

SeckillMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.seckill.mapper.SeckillMapper">
    <!-- 目的：为dao接口方法提供sql语句配置 -->
    <update id="reduceNumber">
        <!-- 具体的sql -->
        UPDATE seckill
        SET number = number - 1
        WHERE
        seckill_id = #{seckillId}
        AND start_time <![CDATA[ <= ]]> #{killTime}
        AND end_time >= #{killTime}
        AND number > 0
    </update>

    <select id="queryById" resultType="Seckill" parameterType="long">
        SELECT
            seckill_id,
            NAME,
            number,
            start_time,
            end_time,
            create_time
        FROM
            seckill
        WHERE
            seckill_id = #{seckillId}
    </select>

    <select id="queryAll" resultType="Seckill">
        SELECT
            seckill_id,
            NAME,
            number,
            start_time,
            end_time,
            create_time
        FROM
            seckill
        ORDER BY
            create_time DESC
        LIMIT #{offset},
            #{limit}
    </select>
</mapper>
```

SuccessKilledMapper.xml

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
        PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="org.seckill.mapper.SuccessKilledMapper">
    <!--如果出现主键冲突，报错-->
    <insert id="insertSuccessKilled">
        insert ignore into success_killed (seckill_id, user_phone,state)
        values (#{seckillId}, #{userPhone},0)
    </insert>

    <select id="queryByIdWithSeckill" resultType="SuccessKilled">
        select
            sk.seckill_id,
            sk.user_phone,
            sk.create_time,
            sk.state,
            s.seckill_id "seckill.seckill_id",
            s.name "seckill.name",
            s.number "seckill.number",
            s.start_time "seckill.start_time",
            s.end_time "seckill.end_time",
            s.create_time "seckill.create_time"
        from success_killed sk
        inner join seckill s on sk.seckill_id = s.seckill_id
        where sk.seckill_id = #{seckillId} and sk.user_phone = #{userPhone}
    </select>
</mapper>
```

4、配置 mybatis 

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
        PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
        "http://mybatis.org/dtd/mybatis-3-config.dtd">
<configuration>
    <!-- 配置全局属性 -->
    <settings>
        <!-- 使用jdbc的getGeneratedKeys获取数据库自增主键值 -->
        <setting name="useGeneratedKeys" value="true" />

        <!-- 使用列别名替换列名 默认:true -->
        <setting name="useColumnLabel" value="true" />

        <!-- 开启驼峰命名转换:Table{create_time} -> Entity{createTime} -->
        <setting name="mapUnderscoreToCamelCase" value="true" />
    </settings>
</configuration>
```

5、配置 JDCB 配置

在 resource 文件夹下建立 jdbc 的配置文件 `jdbc.properties`

```properties
jdbc.driver=com.mysql.jdbc.Driver
jdbc.url=jdbc:mysql://127.0.0.1:3306/seckill?useUnicode=true&characterEncoding=utf8
jdbc.username=root
jdbc.password=root  #根据个人数据库情况配置
```

6、整合 spring

在 resource 文件夹下建立 spring 文件夹存放和 spring 配置有关的文件，建立 `spring-dao.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd
	http://www.springframework.org/schema/context
	http://www.springframework.org/schema/context/spring-context.xsd">
    <!-- 配置整合mybatis过程 -->
    <!-- 1.配置数据库相关参数properties的属性：${url} -->
    <context:property-placeholder location="classpath:jdbc.properties" />

    <!-- 2.数据库连接池 -->
    <bean id="dataSource" class="com.mchange.v2.c3p0.ComboPooledDataSource">
        <!-- 配置连接池属性 -->
        <property name="driverClass" value="${jdbc.driver}" />
        <property name="jdbcUrl" value="${jdbc.url}" />
        <property name="user" value="${jdbc.username}" />
        <property name="password" value="${jdbc.password}" />

        <!-- c3p0连接池的私有属性 -->
        <property name="maxPoolSize" value="30" />
        <property name="minPoolSize" value="10" />
        <!-- 关闭连接后不自动commit -->
        <property name="autoCommitOnClose" value="false" />
        <!-- 获取连接超时时间 -->
        <property name="checkoutTimeout" value="10000" />
        <!-- 当获取连接失败重试次数 -->
        <property name="acquireRetryAttempts" value="2" />
    </bean>

    <!-- 3.配置SqlSessionFactory对象 -->
    <bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
        <!-- 注入数据库连接池 -->
        <property name="dataSource" ref="dataSource" />
        <!-- 配置MyBaties全局配置文件:mybatis-config.xml -->
        <property name="configLocation" value="classpath:mybatis-config.xml" />
        <!-- 扫描entity包 使用别名 -->
        <property name="typeAliasesPackage" value="org.seckill.pojo.entity" />
        <!-- 扫描sql配置文件:mapper需要的xml文件 -->
        <property name="mapperLocations" value="classpath:mybatis/mapper/*.xml" />
    </bean>

    <!-- 4.配置扫描Dao接口包，动态实现Dao接口，注入到soring容器中 -->
    <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
        <!-- 注入sqlSessionFactory -->
        <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
        <!-- 给出需要扫描Dao接口包 -->
        <property name="basePackage" value="org.seckill.mapper" />
    </bean>
</beans>
```

7、测试

在 mapper 接口上使用快捷键 `ctrl+shift+t`快捷创建测试文件

SeckillMapperTest （需要导入 spring-test 依赖否则无法使用）

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillMapperTest {

    @Autowired
    private SeckillMapper seckillMapper;

    @Test
    public void queryById() {
        long id = 1002;
        Seckill seckill = seckillMapper.queryById(id);
        System.out.println(seckill.getName());
        System.out.println(seckill);
    }

    @Test
    public void queryAll() {
        List<Seckill> seckills = seckillMapper.queryAll(0, 100);
        System.out.println(seckills);
    }

    @Test
    public void reduceNumber() {
        Date killTime = new Date();
        int updateCount = seckillMapper.reduceNumber(1000L, killTime);
        System.out.println(updateCount);
    }
}
```

SuccessKilledMapperTest 类

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledMapperTest {


    @Resource
    private SuccessKilledMapper successKilledMapper;

    @Test
    public void insertSuccessKilled() {
        long id = 1001L;
        long phone = 182222213L;
        int i = successKilledMapper.insertSuccessKilled(id, phone);
        System.out.println(i);
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1001L;
        long phone = 182222213L;
        SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(id,phone);
        System.out.println("==========");
        System.out.println("successKilled "+successKilled);
        System.out.println(successKilled.getSeckill());
        System.out.println(successKilled.getSeckill().getNumber());

    }
}
```

## 4、整合 Service 层

```
设计 service 业务接口需要站在 使用者 角度
接口设计三个反面：方法定义粒度、参数、返回类型
```

接口设计前准备，需要设计需要抛出的异常

```java
public class SeckillException extends RuntimeException {
    public SeckillException() {
    }

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
 // 秒杀关闭异常
public class SeckillCloseException extends SeckillException {
    public SeckillCloseException() {
    }

    public SeckillCloseException(String message) {
        super(message);
    }
}
//重复秒杀异常(运行期异常)
public class RepeatKillException extends SeckillException{
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

返回状态

```java

public enum SeckillStatEnum {
    SUCCESS(1,"秒杀成功"),
    END(0,"秒杀结束"),
    REPEAT_KILL(-1, "重复秒杀"),
    INNER_ERROR(-2, "系统异常"),
    DATA_REWRITE(-3,"数据篡改");
    private int state;

    private String stateInfo;

    SeckillStatEnum(int state, String stateInfo) {
        this.state = state;
        this.stateInfo = stateInfo;
    }

    public int getState() {
        return state;
    }

    public String getStateInfo() {
        return stateInfo;
    }

    public static SeckillStatEnum stateOf(int index){
        for (SeckillStatEnum state : values()){
            if (state.getState() == index){
                return state;
            }
        }
        return null;
    }
}
```

Dto

Exposer

```java

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exposer {

    // 是否开启秒杀
    private boolean exposed;

    // 对接口加密
    private String md5;
    //id
    private long seckillId;

    // 系统当前时间(毫秒)
    private long now;
    // 开启时间
    private long start;
    //结束时间
    private long end;

    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    public Exposer(boolean exposed, long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }
}
```

SeckillExecution

```java
@Data
public class SeckillExecution {

    private long seckillId;

    // 秒杀状态
    private int state;
    // 秒杀返回信息
    private String stateInfo;
    // 秒杀成功对象
    private SuccessKilled successKilled;

    public SeckillExecution(long seckillId, SeckillStatEnum statEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    // 返回失败的构造函数
    public SeckillExecution(long seckillId, SeckillStatEnum statEnum) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
    }
}
```



1、SeckillService 秒杀接口设计

```java

public interface SeckillService {

    /**
     * 查询所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开启时输出秒杀接口地址，否则输出系统时间和秒杀时间
     * 不到时间不放出秒杀地址
     * @param seckillId
     */
    Exposer exportSeckillUrl(long seckillId);


    /**
     * 执行秒杀
     * @param seckillId
     * @param userPhone
     * @param md5
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws SeckillException, RepeatKillException, SeckillCloseException;

}
```

对应的实现类

```java
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillMapper seckillMapper;
    @Autowired
    private SuccessKilledMapper successKilledMapper;

    private final String slat = "1dsaw43dsf4r4fdsf2r";

    public List<Seckill> getSeckillList() {
        return seckillMapper.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillMapper.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillMapper.queryById(seckillId);
        if (seckill == null){
            return new Exposer(false,seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        System.out.println("now====>"+nowTime.getTime());
        System.out.println("start====>"+startTime.getTime());
        System.out.println("end====>"+endTime.getTime());
        if (nowTime.getTime() < startTime.getTime()
            || nowTime.getTime() > endTime.getTime()){
             return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(),endTime.getTime());
        }
        // 转化特定字符串过程，不可逆
        String md5 = getMD5(seckillId); //TODO
        return new Exposer(true, md5, seckillId);
    }

    private String getMD5(long seckillId){
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Transactional
    /**
     * 使用租界控制事务方法：
     * 1：明确标注书屋的编程风格
     * 2：保证事务方法的执行时间尽可能短，不穿插其他网络调用
     * 3：不是所有的方法都需要事务
     */
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))){
            throw new SeckillException("seckill data rewrite");
        }
        // 减少库存
        Date nowTime = new Date();


        try {
            int updateCount = seckillMapper.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0){
                // 没有更新记录
                throw new SeckillCloseException("seckill is closed");
            }else {
                //  减少库存成功，记录购买行为
                int insertCount = successKilledMapper.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0){
                    // 重复秒杀
                    throw new RepeatKillException("seckill repeat");
                } else {
                    SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS,successKilled);
                }
            }

        }catch (SeckillCloseException e1){
            throw e1;
        }catch (RepeatKillException e2){
            throw e2;
        }catch (Exception e){
            logger.error(e.getMessage(), e);
            // 编译器异常改成运行期异常，方便事务回滚
            throw new SeckillException("seckill inner error");
        }
    }
}

```

2、sping 整合配置文件

创建 `spring-service.xml`

```
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:tx="http://www.springframework.org/schema/tx"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
    	http://www.springframework.org/schema/beans/spring-beans.xsd
	    http://www.springframework.org/schema/context
	    http://www.springframework.org/schema/context/spring-context.xsd
	    http://www.springframework.org/schema/tx
	    http://www.springframework.org/schema/tx/spring-tx.xsd">

    <!--配置扫描包,包括子包-->
    <context:component-scan base-package="org.seckill.service"/>


    <!--配置事务管理器-->
    <bean id="transactionManager" class="org.springframework.jdbc.datasource.DataSourceTransactionManager">
        <!--注入 数据库 连接池-->
        <property name="dataSource" ref="dataSource"/>
    </bean>

    <!--配置基于注解的声明式书屋，默认通过注解方式管理-->
    <tx:annotation-driven transaction-manager="transactionManager"/>

</beans>
```

3、配置 logback.xml

logback 官网：<http://logback.qos.ch/manual/configuration.html>

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<configuration>

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <!-- encoders are assigned the type
             ch.qos.logback.classic.encoder.PatternLayoutEncoder by default -->
        <encoder>
            <pattern>%d{HH:mm:ss.SSS} [%thread] %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="debug">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```



4、测试，这里测试需要调整数据库中的秒杀开始和结束时间

```java
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}",list);
    }

    @Test
    public void getById() {
        long id = 1000;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}",exposer);
        //exposer=Exposer(exposed=true, md5=bb9b50d6d0d6d4959dfae710151e5f33, seckillId=1000, now=0, start=0, end=0)
    }


    @Test
    public void executeSeckill() {
        long id = 1000;
        long phone = 18226568383L ;
        String md5 ="bb9b50d6d0d6d4959dfae710151e5f33";
        SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
        logger.info("execution={}",execution);

    }

    @Test
    public void testSeckillLogic(){
        long id = 1002;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()){
            logger.info("exposer={}",exposer);
            long phone = 18226568383L ;
            String md5 =exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("execution={}",execution);
            } catch (RepeatKillException e){
                logger.error(e.getMessage());
            } catch (SeckillException e){
                logger.error(e.getMessage());
            }

        }else {
            logger.warn("esposer={}",exposer);
        }
    }
}
```



## 5、整合 web 层

**前端页面流程**

![1586764302146](http://images.vsnode.com/mynotes-images/202004/13/155145-451214.png)

**秒杀流程逻辑**

![1586764385034](http://images.vsnode.com/mynotes-images/202004/13/155305-552723.png)

### Restful

- URL合理的表达方式
- 资源状态和状态转移

**restful 规范**

- /模块/资源/{标识}/集合/...

- GET 查询
- POST 修改删除
- PUT 修改
- DELETE 删除

**秒杀 API 的URL 设计**

![1586764815318](E:\笔记\秒杀项目.assets\1586764815318.png)

### SpringMVC 

Sping MVC 在秒杀系统中的运行流程

![1586764956761](http://images.vsnode.com/mynotes-images/202004/13/160240-101267.png)

HTTP 请求地址映射原理

![1586765043468](http://images.vsnode.com/mynotes-images/202004/18/142248-664463.png)

web 层的实现方式是先整合 springMVC

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:mvc="http://www.springframework.org/schema/mvc"
       xmlns:context="http://www.springframework.org/schema/context"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
	http://www.springframework.org/schema/beans/spring-beans.xsd http://www.springframework.org/schema/mvc https://www.springframework.org/schema/mvc/spring-mvc.xsd http://www.springframework.org/schema/context https://www.springframework.org/schema/context/spring-context.xsd">
    <!--配置 springMVC-->
    <!--1：配置springMVC注解配置-->
    <!-- 简化配置：
        （1）自动注册 DefaultAnnotationHandlerMapping，AnnotationMethodHandlerAdapter
        （2）提供一系列：数据绑定，数字和日期的 format ，@NumberFormat，@DataTimeFormat，
            xml，json 默认读写支持
    -->
    <mvc:annotation-driven/>
    <!-- 2：servlet-mapping 映射路径 “/”-->
    <!--   （1）静态资源默认处理：js，gif，png
           （2）允许使用“/”做整体映射-->
    <mvc:default-servlet-handler/>

    <!--3:配置jsp 显示 ViewResolver-->
    <bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
        <property name="viewClass" value="org.springframework.web.servlet.view.JstlView"/>
        <property name="prefix" value="/WEB-INF/jsp/"/>
        <property name="suffix" value=".jsp"/>
    </bean>
    <!--4：扫描 web 相关的bean-->
    <context:component-scan base-package="org.seckill.controller"/>
</beans>
```

然后通过 bootstrap 编写两个 list 和 detail 两个 jsp 页面。

对传输过来的数据进行处理展示，秒杀逻辑通过 js 做一些判断，js 模块化的方式交互。



## 6、秒杀优化

### 能够优化的点

**秒杀地址接口**

**秒杀操作优化** 

![1587191759858](E:\笔记\秒杀项目.assets\1587191759858.png)

成本分析

![1587191850187](http://images.vsnode.com/mynotes-images/202004/18/143730-960706.png)

使用 MySQL 的瓶颈分析 (但 update 4w次qps，正常 500次 qps)

![1587192059372](http://images.vsnode.com/mynotes-images/202004/18/144104-899386.png)

MySQL 中解决问题

![1587192363547](http://images.vsnode.com/mynotes-images/202004/18/144604-575262.png)

**优化总结**

- 前端控制：暴露接口，防止按钮重复
- 动静数据分离：CDN缓存，后端缓存
- 事务竞争优化：减少事务锁时间（ACID）

### Redis 优化地址暴露







### 通过缩短 update 行级锁时间

原本

![1587279773549](http://images.vsnode.com/mynotes-images/202004/19/150256-4777.png)

降低 update 是 rowLock 的时间，缩短了一倍网络延迟和GC

![1587279764505](http://images.vsnode.com/mynotes-images/202004/19/150252-298782.png)

代码调整为：

```java
public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
    throws SeckillException, RepeatKillException, SeckillCloseException {
    if (md5 == null || !md5.equals(getMD5(seckillId))){
        throw new SeckillException("seckill data rewrite");
    }
    // 减少库存
    Date nowTime = new Date();
    try {
        //  减少库存成功，记录购买行为
        int insertCount = successKilledMapper.insertSuccessKilled(seckillId, userPhone);
        if (insertCount <= 0){
            // 重复秒杀
            throw new RepeatKillException("seckill repeat");
        } else {
            int updateCount = seckillMapper.reduceNumber(seckillId, nowTime);
            if (updateCount <= 0){
                // 没有更新记录
                throw new SeckillCloseException("seckill is closed");
            } else {
                SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS,successKilled);
            }
        }
    }catch (SeckillCloseException e1){
        throw e1;
    }catch (RepeatKillException e2){
        throw e2;
    }catch (Exception e){
        logger.error(e.getMessage(), e);
        // 编译器异常改成运行期异常，方便事务回滚
        throw new SeckillException("seckill inner error");
    }
}
```

### 事务 SQL 在MySQL 段执行（存储过程）

存储过程的目的是让update insert 能够在本地进行事务缩短了时间



## 常见错误汇总

**配置好 tomcat 访问 index 可以访问，访问 controller 中的请求出现 500**

![1586793446330](http://images.vsnode.com/mynotes-images/202004/18/141127-64452.png)

后台报错信息：org.apache.catalina.core.StandardWrapperValve.invoke Allocate exception for servlet [seckill-dispatcher]

出现原因 target/WEB-INF 没有 lib

在IDEA中 artifacts 中选中项目添加 lib 目录，并添加所有的依赖，后面如果还出现此类 dispatcher 层面的报错仍然需要这样修改

![1586793688052](http://images.vsnode.com/mynotes-images/202004/14/000128-384631.png)

配置完成后重新启动查看测试



**设置Cookie 时出现黑色屏幕**

无法点击秒杀号码输入，这是因为 bootstrap 版本问题，修改版本为 3.3.0 即可

![1587140536865](http://images.vsnode.com/mynotes-images/202004/18/002218-401828.png)

