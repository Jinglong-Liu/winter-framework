package com.github.ljl.framework.winter.test.jdbc.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 13:55
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
