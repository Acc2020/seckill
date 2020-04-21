package com.vsnode.service;

import com.vsnode.exception.RepeatKillException;
import com.vsnode.exception.SeckillCloseException;
import com.vsnode.exception.SeckillException;
import com.vsnode.pojo.dto.Exposer;
import com.vsnode.pojo.dto.SeckillExecution;
import com.vsnode.pojo.entity.Seckill;
import org.springframework.stereotype.Service;

import java.util.List;

/*
    @author www.github.com/Acc2020
    @date  2020/4/20
*/

public interface SeckillService {

    /**
     * 查询所有秒杀记录
     * @return
     */
    List<Seckill> getSeckillList();

    /**
     * 查询单个秒杀记录通过id
     * @param seckillId
     * @return
     */
    Seckill getById(long seckillId);

    /**
     * 秒杀开始时才输出秒杀地址，否则输出系统时间和秒杀时间，用于等待，
     * 不到时间不放出秒杀地址，地址使用加密
     * @param seckillId
     * @return
     */
    Exposer exportSeckillUrl(long seckillId);

    /**
     *
     * @param seckillId
     * @param userPhone
     * @param md5
     * @return
     */
    SeckillExecution executeSeckill(long seckillId, long userPhone, String md5)
        throws RepeatKillException, SeckillCloseException, SeckillException;
}
