package com.github.ljl.framework.winter.webmvc.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 22:44
 **/

public class MissingServletRequestParameterException extends WebMvcException {
    public MissingServletRequestParameterException(String message) {
        super(message);
    }
}
