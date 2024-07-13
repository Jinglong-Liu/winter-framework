package com.github.ljl.framework.winter.context.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 11:54
 **/

public class UnsatisfiedDependencyException extends RuntimeException {
    public UnsatisfiedDependencyException(String message) {
        super(message);
    }
}
