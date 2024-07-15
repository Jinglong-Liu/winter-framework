package com.github.ljl.framework.winter.webmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SessionAttribute {
    String value();

    boolean required() default false;
}
