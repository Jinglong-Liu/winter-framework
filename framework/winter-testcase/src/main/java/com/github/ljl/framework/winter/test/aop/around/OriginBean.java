package com.github.ljl.framework.winter.test.aop.around;

import com.github.ljl.framework.winter.aop.annotation.Around;
import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.context.annotation.Value;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 15:53
 **/

@Component
@Around("aroundInvocationHandler")
public class OriginBean {
    @Value("${winter.customer.name}")
    public String name;

    @Polite
    public String hello() {
        return "Hello, " + name + ".";
    }

    public String morning() {
        return "Morning, " + name + ".";
    }
}
