package org.seckill.exception;

/*
    @author www.github.com/Acc2020
    @date  2020/4/13
*/
public class SeckillException extends RuntimeException {
    public SeckillException() {
    }

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }
}
