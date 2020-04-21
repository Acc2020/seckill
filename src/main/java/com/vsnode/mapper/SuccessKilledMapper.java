package com.vsnode.mapper;

import com.vsnode.pojo.entity.SuccessKilled;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/*
    @author www.github.com/Acc2020
    @date  2020/4/19
*/
@Mapper
@Repository
public interface SuccessKilledMapper {

    /**
     * 根据购买明细，可以过滤重复秒杀
     * @param seckillId
     * @param userPhone
     * @return
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据 id 查询 successKilled 携带秒杀产品对象，使用 sql 的链接
     * @param seckillId
     * @param userPhone
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId")long seckillId, @Param("userPhone") long userPhone);
}
