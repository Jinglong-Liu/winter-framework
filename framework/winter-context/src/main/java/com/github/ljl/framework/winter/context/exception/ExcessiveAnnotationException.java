package com.github.ljl.framework.winter.context.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 13:19
 **/

public class ExcessiveAnnotationException extends BeanCreationException {
    public ExcessiveAnnotationException(String message) {
        super(message);
    }

}
