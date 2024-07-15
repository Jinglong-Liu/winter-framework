package com.github.ljl.framework.winter.webmvc;

import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.boot.annotation.WinterBootApplication;
import org.junit.jupiter.api.Test;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 10:47
 **/

@WinterBootApplication
public class WinterWebMvcTest {
    @Test
    void test() {
        WinterApplication.run(WinterWebMvcTest.class);
    }
}
