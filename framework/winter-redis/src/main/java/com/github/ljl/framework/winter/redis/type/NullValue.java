package com.github.ljl.framework.winter.redis.type;

import java.io.Serializable;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-17 13:49
 **/

public final class NullValue implements Serializable {

    public static final Object INSTANCE = new NullValue();

    private static final long serialVersionUID = 1L;


    private NullValue() {
    }

    private Object readResolve() {
        return INSTANCE;
    }


    @Override
    public boolean equals(Object obj) {
        return (this == obj || obj == null);
    }

    @Override
    public int hashCode() {
        return NullValue.class.hashCode();
    }

    @Override
    public String toString() {
        return "null";
    }

}