package com.vsnode.dao.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import com.vsnode.config.JedisConfig;
import com.vsnode.pojo.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

/*
    @author www.github.com/Acc2020
    @date  2020/4/21
*/
@Component
public class RedisDao {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisTemplate redisTemplate;

    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);


    /**
     * 反序列化的过程
     *
     * @param seckillId
     * @return
     */
    public Seckill getSeckill(long seckillId) {

        /*
         * redisTemplate访问redis
         */
        // String key = "seckill:" + seckillId;
        // Seckill seckill = redisTemplate.opsForValue().get(key);
        // if (seckill!=null) {
        // logger.info("从Redis中获取数据：" + seckill.toString());
        // }
        // return seckill;

        /*
         * jedisPool方法访问redis，与慕课网实现相同
         */
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckillId;
                byte[] bytes = jedis.get(key.getBytes());
                if(bytes != null){
                    // 空对象
                    Seckill seckill = schema.newMessage();
                    ProtostuffIOUtil.mergeFrom(bytes, seckill, schema);
                    // 已经被反序列化了
                    return seckill;
                }
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     * 序列化过程
     *
     * @param seckill
     * @return
     */
    public String putSeckill(Seckill seckill) {
        /**
         * redisTemplate访问redis
         */
        /*
         * jedisPool方法访问redis，与慕课网实现相同
         */
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:" + seckill.getSeckillId();
                // 缓冲器，做到缓冲的目的
                byte[] bytes = ProtostuffIOUtil.toByteArray(seckill, schema,
                        LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE));
                // 超时缓存
                int timeout = 60*60;
                String result = jedis.setex(key.getBytes(), timeout, bytes);
                return result;
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }
}