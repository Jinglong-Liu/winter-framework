package com.github.ljl.framework.winter.redis.type;

import com.github.ljl.framework.winter.redis.utils.ObjectUtils;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 20:40
 **/

public class Expiration {

    private long expirationTime;
    private TimeUnit timeUnit;

    protected Expiration(long expirationTime, TimeUnit timeUnit) {

        this.expirationTime = expirationTime;
        this.timeUnit = timeUnit != null ? timeUnit : TimeUnit.SECONDS;
    }

    public long getExpirationTimeInMilliseconds() {
        return getConverted(TimeUnit.MILLISECONDS);
    }

    public long getExpirationTimeInSeconds() {
        return getConverted(TimeUnit.SECONDS);
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public TimeUnit getTimeUnit() {
        return this.timeUnit;
    }

    public long getConverted(TimeUnit targetTimeUnit) {

        return targetTimeUnit.convert(expirationTime, timeUnit);
    }

    public static Expiration seconds(long expirationTime) {
        return new Expiration(expirationTime, TimeUnit.SECONDS);
    }

    public static Expiration milliseconds(long expirationTime) {
        return new Expiration(expirationTime, TimeUnit.MILLISECONDS);
    }

    public static Expiration unixTimestamp(long unixTimestamp, TimeUnit timeUnit) {
        return new Expiration.ExpireAt(unixTimestamp, timeUnit);
    }

    public static Expiration keepTtl() {
        return Expiration.KeepTtl.INSTANCE;
    }

    public static Expiration from(long expirationTime, TimeUnit timeUnit) {

        if (ObjectUtils.nullSafeEquals(timeUnit, TimeUnit.MICROSECONDS)
                || ObjectUtils.nullSafeEquals(timeUnit, TimeUnit.NANOSECONDS)
                || ObjectUtils.nullSafeEquals(timeUnit, TimeUnit.MILLISECONDS)) {
            return new Expiration(timeUnit.toMillis(expirationTime), TimeUnit.MILLISECONDS);
        }

        if (timeUnit != null) {
            return new Expiration(timeUnit.toSeconds(expirationTime), TimeUnit.SECONDS);
        }

        return new Expiration(expirationTime, TimeUnit.SECONDS);
    }

    public static Expiration from(Duration duration) {

        if (duration.toMillis() % 1000 == 0) {
            return new Expiration(duration.getSeconds(), TimeUnit.SECONDS);
        }

        return new Expiration(duration.toMillis(), TimeUnit.MILLISECONDS);
    }

    public static Expiration persistent() {
        return new Expiration(-1, TimeUnit.SECONDS);
    }

    public boolean isPersistent() {
        return expirationTime == -1;
    }

    public boolean isKeepTtl() {
        return false;
    }

    public boolean isUnixTimestamp() {
        return false;
    }

    private static class KeepTtl extends Expiration {

        static Expiration.KeepTtl INSTANCE = new Expiration.KeepTtl();

        private KeepTtl() {
            super(-2, null);
        }

        @Override
        public boolean isKeepTtl() {
            return true;
        }
    }

    private static class ExpireAt extends Expiration {

        private ExpireAt(long expirationTime, TimeUnit timeUnit) {
            super(expirationTime, timeUnit);
        }

        public boolean isUnixTimestamp() {
            return true;
        }
    }
}
