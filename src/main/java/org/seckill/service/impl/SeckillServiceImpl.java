package org.seckill.service.impl;

import org.apache.commons.collections.MapUtils;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillCloseException;
import org.seckill.exception.SeckillException;
import org.seckill.mapper.SeckillMapper;
import org.seckill.mapper.SuccessKilledMapper;
import org.seckill.mapper.cache.RedisDao;
import org.seckill.pojo.dto.Exposer;
import org.seckill.pojo.dto.SeckillExecution;
import org.seckill.pojo.entity.Seckill;
import org.seckill.pojo.entity.SuccessKilled;
import org.seckill.service.SeckillService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
    @author www.github.com/Acc2020
    @date  2020/4/13
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

    private final String slat = "1dsaw43dsf4r4fdsf2r";

    public List<Seckill> getSeckillList() {
        return seckillMapper.queryAll(0,4);
    }

    public Seckill getById(long seckillId) {
        return seckillMapper.queryById(seckillId);
    }

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

    public SeckillExecution executeSeckillPorcedure(long seckillId, long userPhone, String md5)
            throws SeckillException, RepeatKillException, SeckillCloseException {
        if (md5 == null || !md5.equals(getMD5(seckillId))) {
            return new SeckillExecution(seckillId, SeckillStateEnum.DATA_REWRITE);
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId",seckillId);
        map.put("phone",userPhone);
        map.put("killTime",killTime);
        map.put("result",null);
        try {
            seckillMapper.killByProcedure(map);
            // 获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = successKilledMapper.queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStateEnum.SUCCESS, sk);
            } else {
                return new SeckillExecution(seckillId, SeckillStateEnum.stateOf(result));
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStateEnum.INNER_ERROR);
        }
    }
}
