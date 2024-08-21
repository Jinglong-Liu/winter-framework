package com.github.ljl.framework.winter.redis.serializer;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.github.ljl.framework.winter.redis.exception.SerializationException;
import com.github.ljl.framework.winter.redis.type.NullValue;
import com.github.ljl.framework.winter.redis.utils.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-17 12:46
 **/

public class Jackson2JsonRedisSerializer implements RedisSerializer<Object> {

    private final ObjectMapper mapper = new ObjectMapper();

    public Jackson2JsonRedisSerializer() {
        this(null);
    }

    public Jackson2JsonRedisSerializer(String classPropertyTypeName) {
        registerNullValueSerializer(mapper, classPropertyTypeName);

        if (StringUtils.hasText(classPropertyTypeName)) {
            mapper.activateDefaultTypingAsProperty(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL,
                    classPropertyTypeName);
        } else {
            mapper.activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY);
        }
    }


    public static void registerNullValueSerializer(ObjectMapper objectMapper, String classPropertyTypeName) {
        objectMapper.registerModule(new SimpleModule()
                .addSerializer(new NullValueSerializer(classPropertyTypeName)));
    }

    @Override
    public byte[] serialize(Object source) throws SerializationException {

        if (source == null) {
            return new byte[0];
        }

        try {
            return mapper.writeValueAsBytes(source);
        } catch (JsonProcessingException e) {
            throw new SerializationException("Could not write JSON: " + e.getMessage(), e);
        }
    }

    @Override
    public Object deserialize(byte[] source) throws SerializationException {
        return deserialize(source, Object.class);
    }

    public <T> T deserialize(byte[] source, Class<T> type) throws SerializationException {
        if (Objects.isNull(source) || source.length == 0) {
            return null;
        }
        try {
            return mapper.readValue(source, type);
        } catch (Exception ex) {
            throw new SerializationException("Could not read JSON: " + ex.getMessage(), ex);
        }
    }

    private static class NullValueSerializer extends StdSerializer<NullValue> {

        private static final long serialVersionUID = 1999052150548658808L;
        private final String classIdentifier;

        NullValueSerializer(String classIdentifier) {

            super(NullValue.class);
            this.classIdentifier = StringUtils.hasText(classIdentifier) ? classIdentifier : "@class";
        }

        @Override
        public void serialize(NullValue value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {

            jgen.writeStartObject();
            jgen.writeStringField(classIdentifier, NullValue.class.getName());
            jgen.writeEndObject();
        }
    }
}
