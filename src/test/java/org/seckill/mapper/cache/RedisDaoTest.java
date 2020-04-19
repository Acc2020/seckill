package org.seckill.mapper.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.mapper.SeckillMapper;
import org.seckill.pojo.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import redis.clients.jedis.Jedis;

import static org.junit.Assert.*;

/*
    @author www.github.com/Acc2020
    @date  2020/4/19
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class RedisDaoTest {

    private long id = 1003;
    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SeckillMapper seckillMapper;
    @Test
    public void getSeckill() {
        // get // put
        Seckill seckill = redisDao.getSeckill(id);
        System.out.println("xxxxxxxx:"+seckill);
        if (seckill == null){
            seckill = seckillMapper.queryById(id);
            if (seckill != null){
                String result = redisDao.putSeckill(seckill);
                System.out.println("========="+result);
                seckill = redisDao.getSeckill(id);
                System.out.println("========="+seckill);
            }
        }
    }

    @Test
    public void testRedis(){
        Jedis jedis = new Jedis("47.102.106.253",6379);
        System.out.println(jedis.ping());
    }
}