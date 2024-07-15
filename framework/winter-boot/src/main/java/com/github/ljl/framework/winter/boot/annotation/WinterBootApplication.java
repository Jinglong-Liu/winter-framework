package com.github.ljl.framework.winter.boot.annotation;

import com.github.ljl.framework.winter.context.annotation.ComponentScan;

import java.lang.annotation.*;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 21:08
 **/

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan
public @interface WinterBootApplication {
}
