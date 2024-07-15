package com.github.ljl.framework.winter.test.aop.service;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 16:36
 **/

public interface IUserService {
    void createUser(String name, String email, Integer age);
    void createUserWithInnerCall(String name, String email, Integer age);
}
