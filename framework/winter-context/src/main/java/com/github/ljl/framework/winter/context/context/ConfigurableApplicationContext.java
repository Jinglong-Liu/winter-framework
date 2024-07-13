package com.github.ljl.framework.winter.context.context;

import com.github.ljl.framework.winter.context.beans.BeanDefinition;

import java.util.List;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 11:34
 **/

public interface ConfigurableApplicationContext extends ApplicationContext {
    List<BeanDefinition> findBeanDefinitions(Class<?> type);

    BeanDefinition findBeanDefinition(Class<?> type);

    BeanDefinition findBeanDefinition(String name);

    Object createBeanAsEarlySingleton(BeanDefinition def);

    BeanDefinition findBeanDefinition(String name, Class<?> requiredType);
}
