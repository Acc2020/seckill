package org.seckill.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.seckill.exception.RepeatKillException;
import org.seckill.exception.SeckillException;
import org.seckill.pojo.dto.Exposer;
import org.seckill.pojo.dto.SeckillExecution;
import org.seckill.pojo.entity.Seckill;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

/*
    @author www.github.com/Acc2020
    @date  2020/4/13
*/
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"})
public class SeckillServiceTest {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> list = seckillService.getSeckillList();
        logger.info("list={}", list);
    }

    @Test
    public void getById() {
        long id = 1001;
        Seckill seckill = seckillService.getById(id);
        logger.info("seckill={}", seckill);
    }

    @Test
    public void exportSeckillUrl() {
        long id = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        logger.info("exposer={}", exposer);
        //exposer=Exposer(exposed=true, md5=bb9b50d6d0d6d4959dfae710151e5f33, seckillId=1000, now=0, start=0, end=0)
    }


    @Test
    public void executeSeckill() {
        long id = 1000;
        long phone = 18226568383L;
        String md5 = "bb9b50d6d0d6d4959dfae710151e5f33";
        SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
        logger.info("execution={}", execution);

    }


    @Test
    public void testSeckillLogic() {
        long id = 1002;
        Exposer exposer = seckillService.exportSeckillUrl(id);
        if (exposer.isExposed()) {
            logger.info("exposer={}", exposer);
            long phone = 18226568383L;
            String md5 = exposer.getMd5();
            try {
                SeckillExecution execution = seckillService.executeSeckill(id, phone, md5);
                logger.info("execution={}", execution);
            } catch (RepeatKillException e) {
                logger.error(e.getMessage());
            } catch (SeckillException e) {
                logger.error(e.getMessage());
            }

        } else {
            logger.warn("esposer={}", exposer);
        }
    }


    @Test
    public void executeSeckillProcedure() {
        long seckillId = 1000;
        long phone = 18226561234L;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillPorcedure(seckillId, phone, md5);
            logger.info(execution.getStateInfo());
        }
    }
}