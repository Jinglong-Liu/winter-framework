package com.github.ljl.framework.winter.webmvc.annotation;

import com.github.ljl.framework.winter.context.annotation.Component;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Service {
    String value() default "";
}
