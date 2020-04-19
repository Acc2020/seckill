package org.seckill.mapper;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.pojo.entity.SuccessKilled;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/*
    @author www.github.com/Acc2020
    @date  2020/4/13
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SuccessKilledMapperTest {


    @Resource
    private SuccessKilledMapper successKilledMapper;

    @Test
    public void insertSuccessKilled() {
        long id = 1001L;
        long phone = 182222213L;
        int i = successKilledMapper.insertSuccessKilled(id, phone);
        System.out.println(i);
    }

    @Test
    public void queryByIdWithSeckill() {
        long id = 1001L;
        long phone = 182222213L;
        SuccessKilled successKilled = successKilledMapper.queryByIdWithSeckill(id,phone);
        System.out.println("==========");
        System.out.println("successKilled "+successKilled);
        System.out.println(successKilled.getSeckill());
        System.out.println(successKilled.getSeckill().getNumber());

    }
}