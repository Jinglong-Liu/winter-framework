package com.github.ljl.framework.winter.aop.annotation;

import java.lang.annotation.*;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 15:14
 **/

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Around {
    /**
     * Invocation handler bean name.
     */
    String value();
}
