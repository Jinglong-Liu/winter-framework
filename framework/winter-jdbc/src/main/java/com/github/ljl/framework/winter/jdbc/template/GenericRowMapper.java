package com.github.ljl.framework.winter.jdbc.template;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:58
 **/

public class GenericRowMapper<T> implements RowMapper<T> {

    private final Class<T> elementType;

    private static final Map<Class<?>, RowMapper<?>> mappers = new HashMap<>();

    static {
        mappers.put(Boolean.class, (RowMapper<Boolean>) (rs, rowNum) -> rs.getBoolean(1));
        mappers.put(Byte.class, (RowMapper<Byte>) (rs, rowNum) -> rs.getByte(1));
        mappers.put(Short.class, (RowMapper<Short>) (rs, rowNum) -> rs.getShort(1));
        mappers.put(Integer.class, (RowMapper<Integer>) (rs, rowNum) -> rs.getInt(1));
        mappers.put(Long.class, (RowMapper<Long>) (rs, rowNum) -> rs.getLong(1));
        mappers.put(Float.class, (RowMapper<Float>) (rs, rowNum) -> rs.getFloat(1));
        mappers.put(Double.class, (RowMapper<Double>) (rs, rowNum) -> rs.getDouble(1));
        mappers.put(String.class, (RowMapper<String>) (rs, rowNum) -> rs.getString(1));
    }

    private GenericRowMapper(Class<T> elementType) {
        this.elementType = elementType;
    }

    public static RowMapper createMapper(Class<?> elementType) {
        return mappers.getOrDefault(elementType, new GenericRowMapper<>(elementType));
    }


    // Object, 字段对应
    @Override
    public T mapRow(ResultSet rs, int rowNum) throws SQLException {
        try {
            T instance = elementType.getDeclaredConstructor().newInstance();
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = metaData.getColumnName(i);
                Field field;
                try {
                    field = elementType.getDeclaredField(columnName);
                } catch (NoSuchFieldException e) {
                    // If no field with the exact name, try to find a case-insensitive match
                    field = null;
                    for (Field f : elementType.getDeclaredFields()) {
                        if (f.getName().equalsIgnoreCase(columnName)) {
                            field = f;
                            break;
                        }
                    }
                    if (field == null) {
                        continue;
                    }
                }
                field.setAccessible(true);
                Object value = rs.getObject(i);
                field.set(instance, value);
            }
            return instance;
        } catch (Exception e) {
            throw new SQLException("Error mapping row to instance of " + elementType.getName(), e);
        }
    }
}

