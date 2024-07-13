package com.github.ljl.framework.winter.context.test.service;

import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.context.annotation.Component;
import lombok.Data;
import lombok.Getter;
import org.omg.CORBA.PRIVATE_MEMBER;

import javax.annotation.Resource;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 13:45
 **/
@Getter
@Component
public class ServiceA {
    @Getter
    @Autowired
    private ServiceB serviceB;

    private ServiceC serviceC;

    public ServiceA(@Autowired ServiceC serviceC) {
        this.serviceC = serviceC;
    }
}
