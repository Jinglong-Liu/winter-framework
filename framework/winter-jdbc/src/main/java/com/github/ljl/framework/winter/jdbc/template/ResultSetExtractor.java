package com.github.ljl.framework.winter.jdbc.template;

import com.github.ljl.framework.winter.jdbc.exception.DataAccessException;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:33
 **/

@FunctionalInterface
public interface ResultSetExtractor<T> {
    T extractData(ResultSet rs) throws SQLException, DataAccessException;
}
