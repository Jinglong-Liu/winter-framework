package com.github.ljl.framework.winter.aop.handler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 15:22
 **/

public abstract class BeforeInvocationHandlerAdapter implements InvocationHandler {
    public abstract void before(Object proxy, Method method, Object[] args);

    @Override
    public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before(proxy, method, args);
        return method.invoke(proxy, args);
    }
}
