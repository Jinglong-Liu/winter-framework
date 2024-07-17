package com.github.ljl.framework.winter.redis.connection;



/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 21:12
 **/

@FunctionalInterface
public interface Converter<S, T> {

    T convert(S source);

    default <U> Converter<S, U> andThen(Converter<? super T, ? extends U> after) {
        assert after != null;
        return (S s) -> {
            T initialResult = convert(s);
            return (initialResult != null ? after.convert(initialResult) : null);
        };
    }
}
