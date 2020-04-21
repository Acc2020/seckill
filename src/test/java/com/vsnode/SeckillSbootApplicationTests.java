package com.vsnode;

import com.vsnode.mapper.SeckillMapper;
import com.vsnode.pojo.entity.Seckill;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SeckillSbootApplicationTests {

    @Autowired
    private SeckillMapper seckillMapper;



    @Test
    public void reduceNumber() {
        int id = 1002;
        // int i = seckillMapper.reduceNumber();
    }

    @Test
    public void queryById() {
        int id = 1002;
        Seckill seckill = seckillMapper.queryById(id);
        System.out.println(seckill);
    }

    @Test
    public void queryAll() {
    }

}
