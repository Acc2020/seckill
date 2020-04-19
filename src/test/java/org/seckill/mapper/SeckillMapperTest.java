package org.seckill.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.seckill.pojo.entity.Seckill;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/*
    @author www.github.com/Acc2020
    @date  2020/4/12
*/
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