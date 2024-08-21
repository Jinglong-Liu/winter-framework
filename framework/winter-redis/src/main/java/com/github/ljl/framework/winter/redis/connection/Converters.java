package com.github.ljl.framework.winter.redis.connection;


import com.github.ljl.framework.winter.redis.core.ValueEncoding;
import com.github.ljl.framework.winter.redis.type.Expiration;
import com.github.ljl.framework.winter.redis.utils.ObjectUtils;
import redis.clients.jedis.args.BitOP;
import redis.clients.jedis.params.GetExParams;
import redis.clients.jedis.params.SetParams;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.AbstractMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 21:09
 **/

class Converters {
    public static <T> Converter<T, T> identityConverter() {
        return t -> t;
    }
    public static Converter<String, Boolean> stringToBooleanConverter() {
        return source -> ObjectUtils.nullSafeEquals("OK", source);
    }
    public static Converter<Long, Boolean> longToBoolean() {
        return source -> source != null && source == 1L;
    }
    public static Converter<Long, Long> secondsToTimeUnit(TimeUnit timeUnit) {
        return seconds -> {
            if (seconds > 0) {
                return timeUnit.convert(seconds, TimeUnit.SECONDS);
            }
            return seconds;
        };
    }
    public static Converter<Long, Duration> secondsToDuration() {
        return seconds -> seconds != null ? Duration.ofSeconds(seconds) : null;
    }

    public static Converter<String, DataType> stringToDataType() {
        return DataType::fromCode;
    }

    public static ValueEncoding toEncoding(byte[] source) {
        return  ValueEncoding.of(toString(source));
    }

    // TODO: charset
    private static String toString(byte[] source) {
        return source == null ? null : new String(source, StandardCharsets.UTF_8);
    }


    public static SetParams toSetCommandExPxArgument(Expiration expiration, SetParams params) {

        SetParams paramsToUse = params == null ? SetParams.setParams() : params;

        if (expiration.isKeepTtl()) {
            return paramsToUse.keepttl();
        }

        if (expiration.isPersistent()) {
            return paramsToUse;
        }

        if (expiration.getTimeUnit() == TimeUnit.MILLISECONDS) {
            return expiration.isUnixTimestamp() ? paramsToUse.pxAt(expiration.getExpirationTime()) : paramsToUse.px(expiration.getExpirationTime());
        }

        return expiration.isUnixTimestamp() ? paramsToUse.exAt(expiration.getConverted(TimeUnit.SECONDS)) : paramsToUse.ex(expiration.getConverted(TimeUnit.SECONDS));
    }

    public static SetParams toSetCommandNxXxArgument(SetOption option) {
        return toSetCommandNxXxArgument(option, SetParams.setParams());
    }

    public static SetParams toSetCommandNxXxArgument(SetOption option, SetParams params) {

        SetParams paramsToUse = params == null ? SetParams.setParams() : params;

        switch (option) {
            case SET_IF_PRESENT:
                return paramsToUse.xx();
            case SET_IF_ABSENT:
                return paramsToUse.nx();
            default:
                return paramsToUse;
        }
    }
    static GetExParams toGetExParams(Expiration expiration) {

        GetExParams params = new GetExParams();

        if (expiration.isPersistent()) {
            return params.persist();
        }

        if (expiration.getTimeUnit() == TimeUnit.MILLISECONDS) {
            if (expiration.isUnixTimestamp()) {
                return params.pxAt(expiration.getExpirationTime());
            }
            return params.px(expiration.getExpirationTime());
        }

        return expiration.isUnixTimestamp() ? params.exAt(expiration.getConverted(TimeUnit.SECONDS))
                : params.ex(expiration.getConverted(TimeUnit.SECONDS));
    }
    public static byte[][] toByteArrays(Map<byte[], byte[]> source) {
        byte[][] result = new byte[source.size() * 2][];
        int index = 0;
        for (Map.Entry<byte[], byte[]> entry : source.entrySet()) {
            result[index++] = entry.getKey();
            result[index++] = entry.getValue();
        }
        return result;
    }

    public static BitOP toBitOp(BitOperation bitOp) {
        switch (bitOp) {
            case AND:
                return BitOP.AND;
            case OR:
                return BitOP.OR;
            case NOT:
                return BitOP.NOT;
            case XOR:
                return BitOP.XOR;
            default:
                throw new IllegalArgumentException();
        }
    }
    public static <K, V> Map.Entry<K, V> entryOf(K key, V value) {
        return new AbstractMap.SimpleImmutableEntry<>(key, value);
    }
}
