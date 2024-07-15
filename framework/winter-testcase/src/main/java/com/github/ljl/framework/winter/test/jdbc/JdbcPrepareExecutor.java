package com.github.ljl.framework.winter.test.jdbc;

import com.github.ljl.framework.winter.jdbc.template.RowMapper;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import static com.github.ljl.framework.winter.test.jdbc.SQL.*;

/**
 * @program: winter-framework
 * @description: 测试直接只用jdbc，不封装
 * @author: ljl
 * @create: 2024-07-15 12:44
 **/

public class JdbcPrepareExecutor implements SqlExecutor {

    @Override
    public void execute(String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        try {
            conn = DriverManager.getConnection(url, username, password);
            stmt = conn.prepareStatement(sql);
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
            } catch (SQLException se2) {
                se2.printStackTrace();
            }
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    @Override
    public <T> List<T> query(String sql, Class<T> clazz) throws SQLException {
        List<T> resultList = new ArrayList<>();
        Connection conn = DriverManager.getConnection(url, username, password);
        PreparedStatement stmt = conn.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();

        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();
        // 获取结果集的列名数组
        String[] columnNames = new String[columnCount];
        for (int i = 1; i <= columnCount; i++) {
            columnNames[i - 1] = metaData.getColumnName(i);
        }

        while (rs.next()) {
            try {
                T obj = clazz.getDeclaredConstructor().newInstance(); // 使用无参构造函数创建对象
                for (int i = 1; i <= columnCount; i++) {
                    Object value = rs.getObject(i);
                    String columnName = columnNames[i - 1];

                    // 使用反射设置对象的属性值
                    Field field = clazz.getDeclaredField(columnName);
                    field.setAccessible(true);
                    field.set(obj, value);
                }
                resultList.add(obj);
            } catch (Exception e) {
                e.printStackTrace(); // 处理反射异常
            }
        }
        try {
            if (stmt != null) stmt.close();
        } catch (SQLException se2) {
            se2.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return resultList;
    }

    @Override
    public <T> List<T> query(String sql, RowMapper<T> rowMapper) throws SQLException {
        List<T> resultList = new ArrayList<>();
        Connection conn = DriverManager.getConnection(url, username, password);
        PreparedStatement stmt = conn.prepareStatement(sql);

        ResultSet rs = stmt.executeQuery();
        int rowNum = 0;
        while(rs.next()) {
            resultList.add(rowMapper.mapRow(rs, rowNum++));
        }

        try {
            if (stmt != null) stmt.close();
        } catch (SQLException se2) {
            se2.printStackTrace();
        }
        try {
            if (conn != null) conn.close();
        } catch (SQLException se) {
            se.printStackTrace();
        }
        return resultList;
    }
}
