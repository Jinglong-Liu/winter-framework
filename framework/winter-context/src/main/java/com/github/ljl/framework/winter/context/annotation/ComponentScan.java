package com.github.ljl.framework.winter.context.annotation;


import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ComponentScan {
    /**
     * Package names to scan. Default to current package.
     */
    String[] value() default {};
}