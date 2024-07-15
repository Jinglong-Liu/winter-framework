package com.github.ljl.framework.winter.test.aop.around;

import java.lang.annotation.*;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 15:52
 **/

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Polite {

}
