package com.vsnode.mapper;

import com.vsnode.pojo.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/*
    @author www.github.com/Acc2020
    @date  2020/4/20
*/
@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("classpath:application.yml")
@SpringBootTest
public class SeckillMapperTest {

    @Autowired
    private SeckillMapper seckillMapper ;

    @Test
    public void queryById() {
        long id = 1000;
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