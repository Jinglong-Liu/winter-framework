package com.github.ljl.framework.winter.test.jdbc;

import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.boot.annotation.WinterBootApplication;
import com.github.ljl.framework.winter.context.context.ApplicationContext;
import com.github.ljl.framework.winter.jdbc.template.JdbcTemplate;
import com.github.ljl.framework.winter.test.jdbc.bean.User;
import com.github.ljl.framework.winter.test.jdbc.service.UserService;

import java.util.List;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 12:39
 **/

@WinterBootApplication
public class App {
    public static void main(String[] args) {
        ApplicationContext context = WinterApplication.run(App.class);
        UserService service = context.getBean(UserService.class);

        service.dropUserTable();

        service.createTable();

        service.insertUser();

        List<User> users = service.selectAllUsers();
        users.forEach(System.out::println);
        service.deleteUser();
        users = service.selectAllUsers();
        users.forEach(System.out::println);
        System.out.println();
        User user = service.selectUserById(5);
        System.out.println(user);

        service.updateEmailByUsername("Jack", "jack@163.com");
        User user1 = service.selectUserById(10);
        System.out.println(user1);
        System.out.println();
        List<String> emails = service.selectEmails();
        emails.forEach(System.out::println);
    }
}
