package com.github.ljl.framework.winter.webmvc.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 22:41
 **/

public class MissingCookieValueException extends WebMvcException {
    public MissingCookieValueException(String message) {
        super(message);
    }
}
