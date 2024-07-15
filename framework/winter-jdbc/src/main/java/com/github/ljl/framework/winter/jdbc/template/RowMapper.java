package com.github.ljl.framework.winter.jdbc.template;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:32
 **/

@FunctionalInterface
public interface RowMapper<T> {
    T mapRow(ResultSet rs, int rowNum) throws SQLException;
}
