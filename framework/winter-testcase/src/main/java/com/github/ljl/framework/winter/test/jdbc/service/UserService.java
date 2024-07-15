package com.github.ljl.framework.winter.test.jdbc.service;

import com.github.ljl.framework.winter.jdbc.template.JdbcTemplate;
import com.github.ljl.framework.winter.test.jdbc.SQL;
import com.github.ljl.framework.winter.test.jdbc.bean.User;
import com.github.ljl.framework.winter.webmvc.annotation.Service;
import jdk.nashorn.internal.scripts.JD;

import javax.annotation.Resource;
import java.util.List;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 13:48
 **/

@Service
public class UserService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    public void createTable() {
        jdbcTemplate.execute(SQL.createUserTableSQL);
    }

    public void insertUser() {
        jdbcTemplate.execute(SQL.insertUserSQL);
    }
    public List<User> selectAllUsers() {
        return jdbcTemplate.queryForList(SQL.selectAllSQL, User.class);
    }
    public void deleteUser() {
        jdbcTemplate.execute(SQL.deleteUserSQL);
    }
    public void dropUserTable() {
        jdbcTemplate.execute(SQL.dropTableSQL);
    }

    public User selectUserById(Integer id) {
        String sql = "SELECT * FROM USER WHERE ID = ?";
        return jdbcTemplate.queryForObject(sql, User.class, id);
    }
    public void updateEmailByUsername(String username, String email) {
        String sql = "UPDATE USER SET EMAIL = ? WHERE USERNAME = ?";
        jdbcTemplate.execute(sql, email, username);
    }

    public List<String> selectEmails() {
        return jdbcTemplate.queryForList(SQL.selectEmailSQL, String.class);
    }
}
