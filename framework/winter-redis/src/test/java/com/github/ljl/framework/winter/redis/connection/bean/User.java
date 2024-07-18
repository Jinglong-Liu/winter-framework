package com.github.ljl.framework.winter.redis.connection.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-18 12:35
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    Integer id;
    String username;
    String email;
    Integer age;

    @Override
    public String toString() {
        return "User id=" + id + " username=" + username + " email=" + email + " age=" + age;
    }
}
