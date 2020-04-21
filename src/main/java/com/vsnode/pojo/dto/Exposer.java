package com.vsnode.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
    @author www.github.com/Acc2020
    @date  2020/4/20
*/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Exposer {
    // 是否开启秒杀
    private boolean exposed;
    // 接口加密
    private String md5;
    // 秒杀 id
    private long seckillId;
    // 系统当前时间 (时间使用毫秒值)
    private long now;
    // 开启时间
    private long start;
    // 结束时间
    private long end;

    public Exposer(boolean exposed, String md5, long seckillId) {
        this.exposed = exposed;
        this.md5 = md5;
        this.seckillId = seckillId;
    }

    public Exposer(boolean exposed, long seckillId, long now, long start, long end) {
        this.exposed = exposed;
        this.seckillId = seckillId;
        this.now = now;
        this.start = start;
        this.end = end;
    }

    public Exposer(boolean exposed, long seckillId) {
        this.exposed = exposed;
        this.seckillId = seckillId;
    }
}
