package com.github.ljl.framework.winter.jdbc.callback;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:30
 **/

@FunctionalInterface
public interface PreparedStatementCallback<T> {
    T doInPreparedStatement(PreparedStatement preparedStatement) throws SQLException;
}
