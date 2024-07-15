package com.github.ljl.framework.winter.test.jdbc;

import com.github.ljl.framework.winter.jdbc.template.RowMapper;

import java.sql.SQLException;
import java.util.List;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:38
 **/

public interface SqlExecutor {
    void execute(String sql) throws SQLException;

    <T> List<T> query(String sql, Class<T> clazz) throws SQLException;

    <T> List<T> query(String sql, RowMapper<T> rowMapper) throws SQLException;
}
