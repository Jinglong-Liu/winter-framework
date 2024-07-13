package com.github.ljl.framework.winter.context.test.bean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 16:02
 **/

public class Admin implements Expr {
    private Logger logger = LoggerFactory.getLogger(Admin.class);
    private String username;
    private String password;
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public String expr() {
        return "Administer:" + username + "," + password;
    }

}
