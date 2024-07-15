package com.github.ljl.framework.winter.jdbc.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 16:16
 **/

public class TransactionException extends RuntimeException {
    public TransactionException(Throwable e) {
        super(e);
    }
}
