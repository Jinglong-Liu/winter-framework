package com.github.ljl.framework.winter.redis.utils;

import java.util.Objects;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-17 13:49
 **/

public class StringUtils {
    public static boolean hasText(String string) {
        return Objects.nonNull(string) && string.length() > 0;
    }
}
