package com.github.ljl.framework.winter.redis.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 10:09
 **/

public class SerializationException extends RuntimeException {
    public SerializationException(String message) {
        super(message);
    }
    public SerializationException(String message, Throwable e) {
        super(message, e);
    }
}
