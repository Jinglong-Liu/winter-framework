package com.github.ljl.framework.winter.webmvc.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GetMapping {
    /**
     * URL mapping.
     */
    String value();
}
