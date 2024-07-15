package com.github.ljl.framework.winter.aop.bean;

import com.github.ljl.framework.winter.aop.annotation.Around;
import com.github.ljl.framework.winter.aop.handler.AnnotationProxyBeanPostProcessor;
import com.github.ljl.framework.winter.context.annotation.Component;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 15:25
 **/

@Component
public class AroundProxyBeanPostProcessor extends AnnotationProxyBeanPostProcessor<Around> {
}
