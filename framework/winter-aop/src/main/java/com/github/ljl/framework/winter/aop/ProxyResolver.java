package com.github.ljl.framework.winter.aop;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.scaffold.subclass.ConstructorStrategy;
import net.bytebuddy.implementation.InvocationHandlerAdapter;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 15:02
 **/

public class ProxyResolver {


    private ProxyResolver(){}

    private static ProxyResolver instance;

    public static ProxyResolver getInstance() {
        if (Objects.isNull(instance)) {
            synchronized (ProxyResolver.class) {
                if (Objects.isNull(instance)) {
                    instance = new ProxyResolver();
                }
            }
        }
        return instance;
    }

    // ByteBuddy实例:
    ByteBuddy byteBuddy = new ByteBuddy();

    // 传入原始Bean、拦截器，返回代理后的实例:
    public <T> T createProxy(T bean, InvocationHandler handler) {
        // 目标Bean的Class类型:
        Class<?> targetClass = bean.getClass();
        // 动态创建Proxy的Class:
        Class<?> proxyClass = this.byteBuddy
                // 子类用默认无参数构造方法:
                .subclass(targetClass, ConstructorStrategy.Default.DEFAULT_CONSTRUCTOR)
                // 拦截所有public方法:
                .method(ElementMatchers.isPublic()).intercept(InvocationHandlerAdapter.of(
                        // 新的拦截器实例:
                        (proxy, method, args) -> {
                            // 将方法调用代理至原始Bean:
                            return handler.invoke(bean, method, args);
                        }))
                // 生成字节码:
                .make()
                // 加载字节码:
                .load(targetClass.getClassLoader()).getLoaded();
        // 创建Proxy实例:
        Object proxy;
        try {
            proxy = proxyClass.getConstructor().newInstance();
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return (T) proxy;
    }
}
