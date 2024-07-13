package com.github.ljl.framework.winter.context.test.bean;

import com.github.ljl.framework.winter.context.annotation.Bean;
import com.github.ljl.framework.winter.context.annotation.Configuration;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 19:04
 **/

@Configuration
public class Bean4Config {
    @Bean(initMethod = "init", destroyMethod = "destroy")
    Bean4 createBean() {
        Bean4 bean4 = new Bean4();
        bean4.setA("create_bean4_a");
        bean4.setB("create_bean4_b");
        bean4.setC("create_bean4_c");
        bean4.setD("create_bean4_d");
        return bean4;
    }
}
