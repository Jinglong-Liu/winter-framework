package com.github.ljl.framework.winter.redis.core;

import com.github.ljl.framework.winter.redis.utils.ObjectUtils;

import java.util.Optional;

public interface ValueEncoding {

    String raw();

    static ValueEncoding of(String encoding) {
        return RedisValueEncoding.lookup(encoding).orElse(() -> encoding);
    }

    enum RedisValueEncoding implements ValueEncoding {

        /**
         * Normal string encoding.
         */
        RAW("raw"), //
        /**
         * 64 bit signed interval String representing an integer.
         */
        INT("int"), //
        /**
         * Space saving representation for small lists, hashes and sorted sets.
         */
        ZIPLIST("ziplist"), //
        /**
         * Encoding for large lists.
         */
        LINKEDLIST("linkedlist"), //
        /**
         * Space saving representation for small sets that contain only integers.ø
         */
        INTSET("intset"), //
        /**
         * Encoding for large hashes.
         */
        HASHTABLE("hashtable"), //
        /**
         * Encoding for sorted sets of any size.
         */
        SKIPLIST("skiplist"), //
        /**
         * No encoding present due to non existing key.
         */
        VACANT(null);

        private final String raw;

        RedisValueEncoding(String raw) {
            this.raw = raw;
        }

        @Override
        public String raw() {
            return raw;
        }

        static Optional<ValueEncoding> lookup(String encoding) {

            for (ValueEncoding valueEncoding : values()) {
                if (ObjectUtils.nullSafeEquals(valueEncoding.raw(), encoding)) {
                    return Optional.of(valueEncoding);
                }
            }
            return Optional.empty();
        }
    }
}
