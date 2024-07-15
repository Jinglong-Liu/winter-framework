package com.github.ljl.framework.winter.webmvc.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 22:27
 **/

public class MissingRequestHeaderException extends WebMvcException {
    public MissingRequestHeaderException(String message) {
        super(message);
    }
}
