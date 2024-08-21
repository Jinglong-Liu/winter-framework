package com.github.ljl.framework.winter.redis.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 21:06
 **/

public class ConvertJedisAccessException extends RuntimeException {
    public ConvertJedisAccessException(Throwable e) {
        super(e);
    }
}
