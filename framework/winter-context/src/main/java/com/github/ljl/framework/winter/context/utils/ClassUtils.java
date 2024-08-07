package com.github.ljl.framework.winter.context.utils;

import com.github.ljl.framework.winter.context.annotation.Bean;
import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.context.exception.BeanDefinitionException;

import javax.swing.text.html.Option;
import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

import static com.github.ljl.framework.winter.context.utils.AnnotationUtils.findAnnotation;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 12:39
 **/

public class ClassUtils {
    /**
     * Get non-arg method by @PostConstruct or @PreDestroy. Not search in super
     * class.
     *
     * <code>
     * @PostConstruct void init() {}
     * </code>
     */
    public static Method findAnnotationMethod(Class<?> clazz, Class<? extends Annotation> annoClass) {
        // try to get declared method:
        List<Method> ms = Arrays.stream(clazz.getDeclaredMethods())
                .filter(m -> m.isAnnotationPresent(annoClass))
                .peek(m -> {
                    if (m.getParameterCount() != 0) {
                        throw new BeanDefinitionException(
                            String.format("Method '%s' with @%s must not have argument: %s", m.getName(), annoClass.getSimpleName(), clazz.getName()));
                    }
                }).collect(Collectors.toList());

        if (ms.isEmpty()) {
            return null;
        }
        if (ms.size() == 1) {
            return ms.get(0);
        }
        throw new BeanDefinitionException(String.format("Multiple methods with @%s found in class: %s", annoClass.getSimpleName(), clazz.getName()));
    }

    /**
     * Get bean name by:
     *
     * <code>
     * @Bean
     * Hello createHello() {}
     * </code>
     */
    public static String getBeanName(Method method) {
        Bean bean = method.getAnnotation(Bean.class);
        String name = bean.value();
        if (name.isEmpty()) {
            name = method.getName();
        }
        return name;
    }

    /**
     * Get bean name by:
     *
     * <code>
     * @Component
     * public class Hello {}
     * </code>
     */
    public static String getBeanName(Class<?> clazz) {
        String name = "";
        // 查找@Component:
        Component component = clazz.getAnnotation(Component.class);
        if (component != null) {
            // @Component exist:
            name = component.value();
        } else {
            // 未找到@Component，继续在其他注解中查找@Component:
            for (Annotation anno : clazz.getAnnotations()) {
                if (findAnnotation(anno.annotationType(), Component.class) != null) {
                    try {
                        name = (String) anno.annotationType().getMethod("value").invoke(anno);
                    } catch (ReflectiveOperationException e) {
                        throw new BeanDefinitionException("Cannot get annotation value.", e);
                    }
                }
            }
        }
        if (name.isEmpty()) {
            // default name: "HelloWorld" => "helloWorld"
            name = clazz.getSimpleName();
            name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        }
        return name;
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A getAnnotation(Annotation[] annotations, Class<A> annoClass) {
        for (Annotation anno : annotations) {
            if (annoClass.isInstance(anno)) {
                return (A) anno;
            }
        }
        return null;
    }

    /**
     * Get non-arg method by method name. Not search in super class.
     */
    public static Method getNamedMethod(Class<?> clazz, String methodName) {
        try {
            return clazz.getDeclaredMethod(methodName);
        } catch (ReflectiveOperationException e) {
            throw new BeanDefinitionException(String.format("Method '%s' not found in class: %s", methodName, clazz.getName()));
        }
    }
}
