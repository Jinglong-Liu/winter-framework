package com.github.ljl.framework.winter.context.context;

import java.util.List;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 09:25
 **/

public interface ApplicationContext {
    /**
     * @param name
     * @return
     */
    boolean containsBean(String name);

    /**
     *
     * @param name
     * @param <T>
     * @return 对应的bean，找不到抛异常 NoSuchBeanDefinitionException
     */
    <T> T getBean(String name);

    /**
     *
     * @param name
     * @param requiredType
     * @param <T>
     *  @return 对应的bean，找不到抛异常 NoSuchBeanDefinitionException
     */
    <T> T getBean(String name, Class<T> requiredType);

    /**
     * 根据type返回Bean
     * @param requiredType
     * @param <T>
     *  @return 对应的bean，找不到抛异常 NoSuchBeanDefinitionException
     */
    <T> T getBean(Class<T> requiredType);

    /**
     * 根据type返回一组Bean
     * @param requiredType
     * @param <T>
     * @return 对应的Bean集合，找不到返回空List
     */
    <T> List<T> getBeans(Class<T> requiredType);

    /**
     * 关闭并执行所有bean的destroy方法
     */
    void close();
}
