package com.vsnode.pojo.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/*
    @author www.github.com/Acc2020
    @date  2020/4/19
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SuccessKilled {
    private long seckiiId;

    private long userPhone;

    private int state;

    private Date createTime;

    // 使用多对一链接 seckill
    private Seckill seckill;
}
