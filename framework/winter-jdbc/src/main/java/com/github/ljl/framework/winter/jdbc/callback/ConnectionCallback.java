package com.github.ljl.framework.winter.jdbc.callback;

import java.sql.Connection;
import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionCallback<T> {
    T doInConnection(Connection con) throws SQLException;
}
