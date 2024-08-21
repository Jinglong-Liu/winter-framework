package com.github.ljl.framework.winter.redis.utils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 20:30
 **/

public abstract class TimeoutUtils {

    public static boolean hasMillis(Duration duration) {
        return duration.toMillis() % 1000 != 0;
    }

    public static long toSeconds(Duration duration) {
        return roundUpIfNecessary(duration.toMillis(), duration.getSeconds());
    }

    public static long toSeconds(long timeout, TimeUnit unit) {
        return roundUpIfNecessary(timeout, unit.toSeconds(timeout));
    }

    public static double toDoubleSeconds(long timeout, TimeUnit unit) {

        switch (unit) {
            case MILLISECONDS:
            case MICROSECONDS:
            case NANOSECONDS:
                return unit.toMillis(timeout) / 1000d;
            default:
                return unit.toSeconds(timeout);
        }
    }

    public static long toMillis(long timeout, TimeUnit unit) {
        return roundUpIfNecessary(timeout, unit.toMillis(timeout));
    }

    private static long roundUpIfNecessary(long timeout, long convertedTimeout) {
        if (timeout > 0 && convertedTimeout == 0) {
            return 1;
        }
        return convertedTimeout;
    }
}
