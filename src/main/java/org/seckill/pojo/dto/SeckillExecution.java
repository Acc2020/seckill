package org.seckill.pojo.dto;

import lombok.Data;
import org.seckill.enums.SeckillStateEnum;
import org.seckill.pojo.entity.SuccessKilled;

/*
    @author www.github.com/Acc2020
    @date  2020/4/13
    // 封装秒杀执行后的结果
*/
@Data
public class SeckillExecution {

    private long seckillId;

    // 秒杀状态
    private int state;
    // 秒杀返回信息
    private String stateInfo;
    // 秒杀成功对象
    private SuccessKilled successKilled;

    public SeckillExecution(long seckillId, SeckillStateEnum statEnum, SuccessKilled successKilled) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
        this.successKilled = successKilled;
    }

    // 返回失败的构造函数
    public SeckillExecution(long seckillId, SeckillStateEnum statEnum) {
        this.seckillId = seckillId;
        this.state = statEnum.getState();
        this.stateInfo = statEnum.getStateInfo();
    }
}
