package com.github.ljl.framework.winter.context.annotation;

import java.lang.annotation.*;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 12:33
 **/

@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Value {

    String value();

    boolean required() default false;
}
