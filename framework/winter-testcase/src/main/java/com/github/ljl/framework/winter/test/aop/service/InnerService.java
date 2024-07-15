package com.github.ljl.framework.winter.test.aop.service;

import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.jdbc.annotation.Transactional;
import com.github.ljl.framework.winter.jdbc.template.JdbcTemplate;
import com.github.ljl.framework.winter.webmvc.annotation.Service;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 16:36
 **/

@Service
@Transactional
public class InnerService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createUser(String name, String email, Integer age) {
        jdbcTemplate.execute("INSERT INTO user (username, email, age) VALUES (?, ?, ?)", name, email, age);
        // Simulate some other operations
        if (true) { // Simulate an exception to test rollback
            throw new RuntimeException("Simulated exception");
        }
    }
}
