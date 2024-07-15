package com.github.ljl.framework.winter.jdbc.template;

import com.github.ljl.framework.winter.jdbc.callback.*;
import com.github.ljl.framework.winter.jdbc.exception.DataAccessException;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:26
 **/

public class StandardJdbcTemplate implements JdbcTemplate {

    @Resource
    private DataSource dataSource;

    public StandardJdbcTemplate(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return (T) execute(preparedStatementCreator(sql, args), new PreparedStatementQuerySingleton<>(rowMapper));
    }

    @Override
    public <T> T queryForObject(String sql, Class<T> elementType, Object... args) throws DataAccessException {
        return queryForObject(sql, getRowMapper(elementType), args);
    }

    private class PreparedStatementQuery<T> implements PreparedStatementCallback<T> {
        private PreparedStatementSetter pss;
        private ResultSetExtractor<T> rse;
        public PreparedStatementQuery(PreparedStatementSetter pss, ResultSetExtractor<T> rse) {
            this.pss = pss;
        }
        public T doInPreparedStatement(PreparedStatement ps) throws SQLException {
            ResultSet rs = null;
            try {
                if (pss != null) {
                    pss.setValues(ps);
                }
                rs = ps.executeQuery();
                return rse.extractData(rs);
            }
            finally {
                rs.close();
            }
        }
    }

    private class PreparedStatementQuerySingleton<T> implements PreparedStatementCallback<T> {

        private RowMapper<T> rowMapper;
        public PreparedStatementQuerySingleton(RowMapper<T> rowMapper) {
            this.rowMapper = rowMapper;
        }

        @Override
        public T doInPreparedStatement(PreparedStatement ps) throws SQLException {
            T t = null;
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    if (t == null) {
                        t = rowMapper.mapRow(rs, rs.getRow());
                    } else {
                        throw new DataAccessException("Multiple rows found.");
                    }
                }
            }
            if (t == null) {
                throw new DataAccessException("Empty result set.");
            }
            return t;
        }
    }

    @Override
    public void execute(String sql, Object... args) {
        execute(preparedStatementCreator(sql, args), (preparedStatement) -> {
            preparedStatement.execute();
            return null;
        });
    }

    // 此处真正管理PreparedStatement，使用try-with-resource
    public <T> T execute(PreparedStatementCreator psc, PreparedStatementCallback<T> action) {
        return execute((Connection con) -> {
            // sql被绑定到psc了
            try (PreparedStatement ps = psc.createPreparedStatement(con)) {
                // 实际上就是stmt.execute();
                return action.doInPreparedStatement(ps);
            }
        });
    }

    // 此处真正管理connection
    public <T> T execute(ConnectionCallback<T> action) throws DataAccessException {
        // 获取新连接:
        try (Connection newConn = dataSource.getConnection()) {
            final boolean autoCommit = newConn.getAutoCommit();
            if (!autoCommit) {
                newConn.setAutoCommit(true);
            }
            // 执行
            T result = action.doInConnection(newConn);
            if (!autoCommit) {
                newConn.setAutoCommit(false);
            }
            return result;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // important
    @Override
    public <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException {
        return query(sql, getRowMapper(elementType), args);
    }

    @Override
    public <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return query(sql, rowMapper, args);
    }

    private <T> RowMapper<T> getRowMapper(Class<T> elementType) {
        return GenericRowMapper.createMapper(elementType);
    }


    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException {
        return query0(preparedStatementCreator(sql, args),
                new RowMapperResultSetExtractor<>(rowMapper)
        );
    }

    // 此处管理resultSet
    private <T> T query0(
            PreparedStatementCreator psc, final ResultSetExtractor<T> resultSetExtractor)
            throws DataAccessException {
        return execute(psc, (preparedStatement -> {
            try(ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSetExtractor.extractData(resultSet);
            }
        }));
    }

    private PreparedStatementCreator preparedStatementCreator(String sql, Object... args) {
        return (Connection con) -> {
            PreparedStatement ps = con.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                ps.setObject(i + 1, args[i]);
            }
            return ps;
        };
    }
}
