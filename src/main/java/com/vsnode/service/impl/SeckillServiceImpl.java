package com.vsnode.service.impl;

import com.vsnode.config.RedisConfig;
import com.vsnode.constant.SeckillStateEnum;
import com.vsnode.dao.cache.RedisDao;
import com.vsnode.exception.RepeatKillException;
import com.vsnode.exception.SeckillCloseException;
import com.vsnode.exception.SeckillException;
import com.vsnode.mapper.SeckillMapper;
import com.vsnode.mapper.SuccessKilledMapper;
import com.vsnode.pojo.dto.Exposer;
import com.vsnode.pojo.dto.SeckillExecution;
import com.vsnode.pojo.entity.Seckill;
import com.vsnode.pojo.entity.SuccessKilled;
import com.vsnode.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;


import java.util.Date;
import java.util.List;

/*
    @author www.github.com/Acc2020
    @date  2020/4/20
*/
@Service
public class SeckillServiceImpl implements SeckillService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillMapper seckillMapper;

    @Autowired
    private SuccessKilledMapper successKilledMapper;

    @Autowired
    private RedisDao redisDao;


    // md5 加密加盐
    private final String slat = "1dsaw43dsf4r4fdsf2r";


    @Override
    public List<Seckill> getSeckillList() {
        return seckillMapper.queryAll(0,10);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillMapper.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //  缓存优化通过 redis 降低数据库使用压力，
        /**
         *  get grom cache
         *  if null
         *  get db
         *  else
         *      put cache
         *  locgoin
         */
        // 超时的基础上维护一致性，秒杀一般不允许修改，一般是重建
        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null){
            seckill = seckillMapper.queryById(seckillId);
            if (seckill == null){
                return new Exposer(false,seckillId);
            } else {
                redisDao.putSeckill(seckill);
            }
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();

        // 判断时间关系是否在秒杀时间内
        if (nowTime.getTime() < startTime.getTime()
            || nowTime.getTime() > endTime.getTime()){
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        // 配置 URL 加密，MD5 不可逆
        String md5= getMD5(seckillId);
        return new Exposer(true, md5, seckillId);
    }

    @Override
    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
            throws RepeatKillException, SeckillCloseException, SeckillException {
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
                    // 没有更新记录 rollback
                    throw new SeckillCloseException("seckill is closed");
                } else {
                    // 秒杀成功 commit
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
            throw new SeckillException("seckill inner error"+e.getMessage());
        }
    }

    private String getMD5(long seckillId) {
        String base = seckillId + "/" + slat;
        return DigestUtils.md5DigestAsHex(base.getBytes());
    }
}
