package com.github.ljl.framework.winter.context.test.service;

import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.context.annotation.Component;
import lombok.Data;
import lombok.Getter;

import javax.annotation.Resource;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 14:24
 **/

@Component
@Getter
public class ServiceC {
    @Resource
    ServiceA serviceA;

    @Autowired
    ServiceA serviceD;

    @Autowired
    ServiceB serviceX;
}
