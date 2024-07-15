package com.github.ljl.framework.winter.test.jdbc;

import com.github.ljl.framework.winter.jdbc.template.RowMapper;
import com.github.ljl.framework.winter.jdbc.template.JdbcTemplate;

import javax.annotation.Resource;
import java.sql.SQLException;
import java.util.List;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:44
 **/

public class JdbcTemplateExecutor implements SqlExecutor {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void execute(String sql) throws SQLException {
        jdbcTemplate.execute(sql);
    }

    @Override
    public <T> List<T> query(String sql, Class<T> clazz) throws SQLException {
        return jdbcTemplate.queryForList(sql, clazz);
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws SQLException {
        return jdbcTemplate.queryForList(sql, rowMapper);
    }
}
