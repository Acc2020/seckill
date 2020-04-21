package com.vsnode.service;
import com.vsnode.pojo.dto.Exposer;
import com.vsnode.pojo.dto.SeckillExecution;
import com.vsnode.pojo.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import java.util.List;

import static org.junit.Assert.*;

/*
    @author www.github.com/Acc2020
    @date  2020/4/21
*/
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SeckillServiceTest {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> seckillList = seckillService.getSeckillList();
        logger.info("list={}", seckillList);
    }

    @Test
    public void getById() {
        long id = 1000L;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}",seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long id = 1001L;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}", exposer);
        //exposer=Exposer(exposed=true, md5=83542fddea6365543c3fa4cddff60b1f, seckillId=1001, now=0, start=0, end=0)
    }

    @Test
    public void executeSeckill() {
        long id = 1001L;
        String md5 = "83542fddea6365543c3fa4cddff60b1f";
        long phone = 18226561004L;
        SeckillExecution seckillExecution = seckillService.executeSeckill(id, phone, md5);
        logger.info("seckill={}",seckillExecution);
        //seckill=SeckillExecution(seckillId=1001, state=1, stateInfo=秒杀成功,
        // successKilled=SuccessKilled(seckiiId=0, userPhone=18226561004, state=0, createTime=Tue Apr 21 08:15:23 CST 2020,
        // seckill=Seckill(seckillId=1001, name=1000 元秒杀 ipad Pro, number=198, startTime=Mon Apr 20 08:00:00 CST 2020, endTime=Tue Apr 21 08:00:00 CST 2020, createTime=Mon Apr 13 05:18:46 CST 2020)))
    }
}