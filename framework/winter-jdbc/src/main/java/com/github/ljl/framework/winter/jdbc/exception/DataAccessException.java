package com.github.ljl.framework.winter.jdbc.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:34
 **/

public class DataAccessException extends RuntimeException {
    public DataAccessException(String message) {
        super(message);
    }
    public DataAccessException(Throwable e) {
        super(e);
    }
}
