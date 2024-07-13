package com.github.ljl.framework.winter.context.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 12:43
 **/

public class NoSuchBeanDefinitionException extends BeanDefinitionException {
    public NoSuchBeanDefinitionException() {
    }
    public NoSuchBeanDefinitionException(String message) {
        super(message);
    }

    public NoSuchBeanDefinitionException(Throwable cause) {
        super(cause);
    }

    public NoSuchBeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
