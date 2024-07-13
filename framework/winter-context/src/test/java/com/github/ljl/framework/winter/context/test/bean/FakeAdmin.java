package com.github.ljl.framework.winter.context.test.bean;

import com.github.ljl.framework.winter.context.annotation.Component;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 16:53
 **/

// 被Admin的Primary覆盖
@Component
public class FakeAdmin implements Expr {
    @Override
    public String expr() {
        return null;
    }
}
