package com.github.ljl.framework.winter.redis.connection;

import java.util.EnumSet;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 16:26
 **/

enum DataType {
    NONE("none"),
    STRING("string"),
    LIST("list"),
    SET("set"),
    ZSET("zset"),
    HASH("hash"),
    STREAM("stream");

    private static final Map<String, DataType> codeLookup = new ConcurrentHashMap<>(7);

    static {
        for (DataType type : EnumSet.allOf(DataType.class))
            codeLookup.put(type.code, type);
    }

    private final String code;

    DataType(String name) {
        this.code = name;
    }

    /**
     * Returns the code associated with the current enum.
     *
     * @return code of this enum
     */
    public String code() {
        return code;
    }

    /**
     * Utility method for converting an enum code to an actual enum.
     *
     * @param code enum code
     * @return actual enum corresponding to the given code
     */
    public static DataType fromCode(String code) {
        DataType data = codeLookup.get(code);
        if (data == null)
            throw new IllegalArgumentException("unknown data type code");
        return data;
    }
}
