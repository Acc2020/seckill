
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