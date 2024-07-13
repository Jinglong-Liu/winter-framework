package com.github.ljl.framework.winter.boot.test;

import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.boot.annotation.WinterBootApplication;
import com.github.ljl.framework.winter.boot.test.beans.Bean1;
import com.github.ljl.framework.winter.context.annotation.ComponentScan;
import com.github.ljl.framework.winter.context.context.ApplicationContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 20:56
 **/

@WinterBootApplication
public class ApplicationTest {
    @Test
    void app() {
        ApplicationContext applicationContext = WinterApplication.run(ApplicationTest.class);
        Bean1 bean1 = applicationContext.getBean(Bean1.class);
        Assertions.assertEquals(8082, bean1.getPort());
    }
}
