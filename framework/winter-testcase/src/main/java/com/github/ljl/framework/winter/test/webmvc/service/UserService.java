package com.github.ljl.framework.winter.test.webmvc.service;

import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.webmvc.annotation.Service;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 11:45
 **/

@Service
public class UserService implements IUserService {
    @Override
    public String test() {
        return this.getClass().getName();
    }
}
