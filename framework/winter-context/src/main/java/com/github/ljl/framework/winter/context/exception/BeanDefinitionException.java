package com.github.ljl.framework.winter.context.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 11:53
 **/

public class BeanDefinitionException extends BeansException {
    public BeanDefinitionException() {
    }
    public BeanDefinitionException(String message) {
        super(message);
    }

    public BeanDefinitionException(Throwable cause) {
        super(cause);
    }

    public BeanDefinitionException(String message, Throwable cause) {
        super(message, cause);
    }
}
