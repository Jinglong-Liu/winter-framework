package com.github.ljl.framework.winter.context.utils;

import com.github.ljl.framework.winter.context.exception.BeanDefinitionException;

import java.lang.annotation.Annotation;
import java.lang.annotation.Inherited;
import java.util.*;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 10:09
 **/

public class AnnotationUtils {
    /**
     * @param target 待搜索的入口类
     * @param annoClass 需要查询的注解类的Class
     * @param <A> 注解类
     * @return 找到一个目标返回
     * @throws BeanDefinitionException 找到重复的目标
     */
    public static <A extends Annotation> A findAnnotation(Class<?> target, Class<A> annoClass) {
        A a = target.getAnnotation(annoClass);
        for (Annotation anno : target.getAnnotations()) {
            Class<? extends Annotation> annoType = anno.annotationType();
            if (!annoType.getPackage().getName().equals("java.lang.annotation")) {
                A found = findAnnotation(annoType, annoClass);
                if (found != null) {
                    if (a != null) {
                        throw new BeanDefinitionException("Duplicate @" + annoClass.getSimpleName() + " found on class " + target.getSimpleName());
                    }
                    a = found;
                }
            }
        }
        return a;
    }

    /**
     * @param target 待搜索的入口类
     * @param annoClass 需要查询的注解类的Class
     * @param <A> 注解类
     * @return 所有找到的注解类，包括组合注解
     */
    public static <A extends Annotation> List<A> findAnnotations(Class<?> target, Class<A> annoClass) {
        Set<A> annotations = new HashSet<>();
        findAnnotations(target, annoClass, annotations);
        return new ArrayList<>(annotations);
    }

    private static <A extends Annotation> void findAnnotations(Class<?> target, Class<A> annoClass, Set<A> annotations) {
        A[] foundAnnotations = target.getAnnotationsByType(annoClass);
        annotations.addAll(Arrays.asList(foundAnnotations));

        for (Annotation anno : target.getAnnotations()) {
            Class<? extends Annotation> annoType = anno.annotationType();
            if (!annoType.getPackage().getName().equals("java.lang.annotation")) {
                if (annoType.isAnnotationPresent(Inherited.class)) {
                    continue; // Skip if the annotation is @Inherited
                }
                findAnnotations(annoType, annoClass, annotations);
            }
        }
    }
}
