package com.github.ljl.framework.winter.jdbc.callback;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:31
 **/

public interface PreparedStatementCreator {
    PreparedStatement createPreparedStatement(Connection con) throws SQLException;
}
