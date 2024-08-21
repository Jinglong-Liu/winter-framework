package com.github.ljl.framework.winter.redis.connection;

import redis.clients.jedis.Jedis;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 21:00
 * org.springframework.data.redis.connection.jedis.JedisInvoker
 **/

class JedisInvoker {

    private Synchronizer synchronizer;
    public JedisInvoker(Synchronizer synchronizer) {
        this.synchronizer = synchronizer;
    }

    <R> R just(ConnectionFunction0<R> function) {

        assert function != null;

        return synchronizer.invoke(function::apply,
                Converters.identityConverter(),
                () -> null);
    }

    /**
     * 带参数的另一种写法
     * @param function
     * @param t1
     * @param <R>
     * @param <T1>
     * @return
     */
    <R, T1> R just(ConnectionFunction1<T1, R> function, T1 t1) {

        assert function != null;

        return synchronizer.invoke(it -> function.apply(it, t1),
                Converters.identityConverter(),
                () -> null);
    }



    @FunctionalInterface
    interface Synchronizer {

        @SuppressWarnings({ "unchecked", "rawtypes" })
        default <I, T> T invoke(Function<Jedis, I> callFunction) {
            return (T) doInvoke((Function) callFunction, Converters.identityConverter(), () -> null);
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        default <I, T> T invoke(Function<Jedis, I> callFunction,
                                Converter<I, T> converter,
                                Supplier<T> nullDefault) {

            return (T) doInvoke((Function) callFunction, (Converter<Object, Object>) converter,
                    (Supplier<Object>) nullDefault);
        }

        Object doInvoke(Function<Jedis, Object> callFunction,
                        Converter<Object, Object> converter,
                        Supplier<Object> nullDefault);
    }



    /**
     * A function accepting {@link Jedis} with 0 arguments.
     *
     * @param <R>
     */
    @FunctionalInterface
    interface ConnectionFunction0<R> {

        /**
         * Apply this function to the arguments and return a response.
         * @param connection the connection in use. Never {@literal null}.
         */
        R apply(Jedis connection);
    }

    /**
     * A function accepting {@link Jedis} with 1 argument.
     *
     * @param <T1>
     * @param <R>
     */
    @FunctionalInterface
    interface ConnectionFunction1<T1, R> {

        /**
         * Apply this function to the arguments and return a response.
         * @param connection the connection in use. Never {@literal null}.
         * @param t1 first argument.
         */
        R apply(Jedis connection, T1 t1);
    }

    /**
     * T -> R
     * @param function
     * @return
     */
    public <T> SingleInvocationSpec<T> from(ConnectionFunction0<T> function) {
        return new SingleInvocationSpec<T>() {
            @Override
            public <R> R get(Converter<T, R> converter) {
                return converter.convert(just(function));
            }

            @Override
            public <R> R getOrElse(Converter<T, R> converter, Supplier<R> nullDefault) {
                R result = converter.convert(just(function));
                if (Objects.isNull(result)) {
                    result = nullDefault.get();
                }
                return result;
            }
        };
    }

    /**
     * Represents an element in the invocation pipleline allowing consuming the result by applying a {@link Converter}.
     *
     * @param <S>
     */

    interface SingleInvocationSpec<S> {
        <T> T get(Converter<S, T> converter);
        default <T> T orElse(Converter<S, T> converter, T nullDefault) {
            return getOrElse(converter, () -> nullDefault);
        }
        <T> T getOrElse(Converter<S, T> converter, Supplier<T> nullDefault);
    }
}
