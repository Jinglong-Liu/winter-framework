package com.github.ljl.framework.winter.context.utils;

import com.github.ljl.framework.winter.context.context.ApplicationContext;

import java.util.Objects;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 11:52
 **/

public class ApplicationContextUtils {
    private static ApplicationContext applicationContext = null;

    public static ApplicationContext getRequiredApplicationContext() {
        return Objects.requireNonNull(getApplicationContext(), "ApplicationContext is not set.");
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static void setApplicationContext(ApplicationContext ctx) {
        applicationContext = ctx;
    }
}