package com.github.ljl.framework.winter.aop.exception;

import com.github.ljl.framework.winter.context.exception.BeanCreationException;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 15:13
 **/

public class AopConfigException extends BeanCreationException {
    public AopConfigException() {
    }

    public AopConfigException(String message) {
        super(message);
    }

    public AopConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public AopConfigException(Throwable cause) {
        super(cause);
    }
}
