package org.seckill.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
    @author www.github.com/Acc2020
    @date  2020/4/12
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Seckill {
    private long seckillId;

    private String name;

    private int number;

    private Date startTime;

    private Date endTime;

    private Date createTime;

}
