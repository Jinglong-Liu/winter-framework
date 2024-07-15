package com.github.ljl.framework.winter.test.aop.service;

import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.jdbc.annotation.Transactional;
import com.github.ljl.framework.winter.jdbc.template.JdbcTemplate;
import com.github.ljl.framework.winter.webmvc.annotation.Service;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 16:31
 **/

@Transactional
@Service
public class UserService implements IUserService {

    @Autowired
    InnerService innerService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void createUser(String name, String email, Integer age) {
        jdbcTemplate.execute("INSERT INTO user (username, email, age) VALUES (?, ?, ?)", name, email, age);
        // Simulate some other operations
    }

    @Override
    public void createUserWithInnerCall(String name, String email, Integer age) {
        jdbcTemplate.execute("INSERT INTO user (username, email, age) VALUES (?, ?, ?)", name +"invalid", email, age);
        innerService.createUser(name, email, age);
    }
}
