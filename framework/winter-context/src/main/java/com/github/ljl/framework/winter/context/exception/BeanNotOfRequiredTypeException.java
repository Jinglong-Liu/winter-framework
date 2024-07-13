package com.github.ljl.framework.winter.context.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 12:39
 **/

public class BeanNotOfRequiredTypeException extends BeanDefinitionException {
    public BeanNotOfRequiredTypeException() {
    }
    public BeanNotOfRequiredTypeException(String message) {
        super(message);
    }

    public BeanNotOfRequiredTypeException(Throwable cause) {
        super(cause);
    }

    public BeanNotOfRequiredTypeException(String message, Throwable cause) {
        super(message, cause);
    }
}
