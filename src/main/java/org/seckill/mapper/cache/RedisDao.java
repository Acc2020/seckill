package org.seckill.mapper.cache;

import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.seckill.pojo.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;


/*
    @author www.github.com/Acc2020
    @date  2020/4/19
*/
public class RedisDao {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private JedisPool jedisPool;

    // 通过字节码对应的对象属性传递
    private RuntimeSchema<Seckill> schema = RuntimeSchema.createFrom(Seckill.class);

    public RedisDao(String ip, int port){
        jedisPool = new JedisPool(ip, port);
    }


    public Seckill getSeckill(long seckillId){
        // redis 操作逻辑
        try{
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckillId;
                // 需要实现序列化
                // get -> byte[] -> 反序列化 -> Object(seckillId)
                // 使用自定义序列化
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
        } catch (Exception e){
            logger.error(e.getMessage(),e);
        }
        return null;
    }
    public String putSeckill(Seckill seckill){
        // set Object(seckill) -> 序列化 -> byte[]
        try {
            Jedis jedis = jedisPool.getResource();
            try {
                String key = "seckill:"+seckill.getSeckillId();
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
            logger.error(e.getMessage(),e);
        }
        return null;
    }
}
