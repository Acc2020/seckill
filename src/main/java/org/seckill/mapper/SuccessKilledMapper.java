package org.seckill.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.seckill.pojo.entity.SuccessKilled;

/*
    @author www.github.com/Acc2020
    @date  2020/4/12
*/

public interface SuccessKilledMapper {

    /**
     * 插入购买明细,可以过滤重复秒杀
     * @param seckillId
     * @param userPhone
     * @return 插入的结果集数量，行数（1行）
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 根据 id 查询 SuccessKilled 携带秒杀产品对象，使用 sql 的链接
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);


}
