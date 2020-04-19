package org.seckill.pojo.vo;

import lombok.Data;

/*
    @author www.github.com/Acc2020
    @date  2020/4/13
*/
// 所有的 ajax 请求返回类型，封装成 json 结果
@Data
public class SeckillResult<T> {
    private  boolean success;

    private T data;

    private String error;

    public SeckillResult(boolean success, T data) {
        this.success = success;
        this.data = data;
    }

    public SeckillResult(boolean success, String error) {
        this.success = success;
        this.error = error;
    }
}
