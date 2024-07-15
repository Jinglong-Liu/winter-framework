package com.github.ljl.framework.winter.test.webmvc.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 11:55
 **/


@AllArgsConstructor
@NoArgsConstructor
@Data
public class User {
    private String userId;
    private String username;
    private String password;
    private String desc;
}
