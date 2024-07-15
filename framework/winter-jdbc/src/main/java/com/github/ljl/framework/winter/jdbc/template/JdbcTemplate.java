package com.github.ljl.framework.winter.jdbc.template;

import com.github.ljl.framework.winter.jdbc.exception.DataAccessException;

import java.util.List;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:26
 **/

public interface JdbcTemplate {

    /**
     * 执行SQL语句，包括update, delete, insert, create等都可以
     * @param sql
     * @param args 可选条件
     */
    void execute(String sql, Object... args);

    /**
     * 查询
     * @param sql sql
     * @param rowMapper 自定义映射关系
     * @param args 条件
     * @param <T> 预期返回类型
     * @return
     * @throws DataAccessException
     */
    <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;

    /**
     * @param sql sql
     * @param elementType 预计返回的类型, 字段对应，自动生成RowMapper
     * @param args 条件
     * @param <T>
     * @return
     * @throws DataAccessException
     */
    <T> T queryForObject(String sql, Class<T> elementType, Object... args) throws DataAccessException;

    /**
     * @param sql sql
     * @param elementType 预计返回的类型，字段对应，自动生成RowMapper
     * @param args 条件
     * @param <T>
     * @return
     * @throws DataAccessException
     */
    <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException;

    /**
     * @param sql sql
     * @param rowMapper 自定义映射关系
     * @param args 条件
     * @param <T>
     * @return
     * @throws DataAccessException
     */
    <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;
}
