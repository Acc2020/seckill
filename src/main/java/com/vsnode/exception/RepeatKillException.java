package com.vsnode.exception;

/*
    @author www.github.com/Acc2020
    @date  2020/4/20
*/
public class RepeatKillException extends SeckillException {
    public RepeatKillException(String message) {
        super(message);
    }

    public RepeatKillException(String message, Throwable cause) {
        super(message, cause);
    }
}
