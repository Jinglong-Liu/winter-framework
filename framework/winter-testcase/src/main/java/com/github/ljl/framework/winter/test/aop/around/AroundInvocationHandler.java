package com.github.ljl.framework.winter.test.aop.around;

import com.github.ljl.framework.winter.context.annotation.Component;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 15:56
 **/

@Component
public class AroundInvocationHandler implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 拦截标记了@Polite的方法返回值:
        if (method.getAnnotation(Polite.class) != null) {
            String ret = (String) method.invoke(proxy, args);
            if (ret.endsWith(".")) {
                ret = ret.substring(0, ret.length() - 1) + "!";
            }
            return ret;
        }
        return method.invoke(proxy, args);
    }
}
