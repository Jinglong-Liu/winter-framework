package com.github.ljl.framework.winter.webmvc.annotation;

import com.github.ljl.framework.winter.context.annotation.Component;

import java.lang.annotation.*;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 22:22
 **/

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface RestController {
    String value() default "";
}
