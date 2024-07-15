package com.github.ljl.framework.winter.webmvc.exception;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 07:57
 **/

public class RegisterRepeatPathException extends WebMvcException {
    public RegisterRepeatPathException(String message) {
        super(message);
    }
}
