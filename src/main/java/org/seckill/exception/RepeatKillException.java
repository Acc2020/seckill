package org.seckill.exception;

/*
    @author www.github.com/Acc2020
    @date  2020/4/13
    重复秒杀异常(运行期异常)
*/
public class RepeatKillException extends SeckillException{
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
