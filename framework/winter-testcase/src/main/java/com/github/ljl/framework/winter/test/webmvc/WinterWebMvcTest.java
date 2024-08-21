package com.github.ljl.framework.winter.test.webmvc;

import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.webmvc.annotation.WinterBootWebApplication;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 11:15
 **/

@WinterBootWebApplication
public class WinterWebMvcTest {
    public static void main(String[] args) {
        WinterApplication.run(WinterWebMvcTest.class);
    }
}
