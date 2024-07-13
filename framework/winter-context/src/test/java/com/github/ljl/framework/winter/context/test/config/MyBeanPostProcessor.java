package com.github.ljl.framework.winter.context.test.config;

import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.context.beans.BeanPostProcessor;
import com.github.ljl.framework.winter.context.test.bean.Bean2;
import com.github.ljl.framework.winter.context.test.bean.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 16:36
 **/

@Component
public class MyBeanPostProcessor implements BeanPostProcessor {
    private Logger logger = LoggerFactory.getLogger(MyBeanPostProcessor.class);
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {
        logger.debug("Before Initialization : " + beanName);
        if (bean instanceof User) {
            User user = new User();
            user.setAge("15");
            user.setEmail(((User) bean).getEmail());
            user.setName(((User) bean).getName());
            user.setId(((User) bean).getId());
            return user;
        }
        return bean; // you can return any other object as wel
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        logger.debug("After Initialization : " + beanName);
        if (bean instanceof User) {
            ((User) bean).setAge("30");
        }
        if (bean instanceof Bean2) {
            Bean2 b2 = new Bean2();
            b2.setA(((Bean2) bean).getA());
            b2.setB("bb");
            b2.setC("cc");
            b2.setD("dd");;
            return b2;
        }
        return bean;
    }

    @Override
    public Object postProcessOnSetProperty(Object bean, String beanName) {
        logger.debug("OnSetProperty Initialization : " + beanName);
        if (bean instanceof User) {
            ((User) bean).setEmail("10000@edf.com");
            return bean;
        }
        return bean;
    }
}
