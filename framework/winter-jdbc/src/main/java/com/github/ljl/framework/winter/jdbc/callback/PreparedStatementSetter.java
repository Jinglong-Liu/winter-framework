package com.github.ljl.framework.winter.jdbc.callback;

import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:32
 **/

@FunctionalInterface
public interface PreparedStatementSetter {
    void setValues(PreparedStatement ps) throws SQLException;
}
