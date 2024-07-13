package com.github.ljl.framework.winter.context.test.config;

import com.github.ljl.framework.winter.context.annotation.Bean;
import com.github.ljl.framework.winter.context.annotation.ComponentScan;
import com.github.ljl.framework.winter.context.annotation.Configuration;
import com.github.ljl.framework.winter.context.annotation.Primary;
import com.github.ljl.framework.winter.context.test.bean.Admin;
import com.github.ljl.framework.winter.context.test.bean.Expr;
import com.github.ljl.framework.winter.context.test.service.ServiceB;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 13:48
 **/

@ComponentScan(value = "com.github.ljl.framework.winter.context.test")
@Configuration
public class TestConfig {

    @Primary
    @Bean
    Admin createWhat() {
        return new Admin("admin", "root");
    }
}
