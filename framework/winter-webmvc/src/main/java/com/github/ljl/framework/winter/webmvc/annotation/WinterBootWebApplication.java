package com.github.ljl.framework.winter.webmvc.annotation;

import com.github.ljl.framework.winter.context.annotation.ComponentScan;
import com.github.ljl.framework.winter.context.annotation.Import;
import com.github.ljl.framework.winter.webmvc.config.WebServerConfig;

import java.lang.annotation.*;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 10:06
 **/

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ComponentScan
@Import(WebServerConfig.class)
public @interface WinterBootWebApplication {

}
