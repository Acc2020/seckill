package com.vsnode.exception;

/*
    @author www.github.com/Acc2020
    @date  2020/4/20
*/
public class SeckillException extends RuntimeException {
    public SeckillException() {
        super();
    }

    public SeckillException(String message) {
        super(message);
    }

    public SeckillException(String message, Throwable cause) {
        super(message, cause);
    }

}
