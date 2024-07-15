package com.github.ljl.framework.winter.test.aop;

import com.github.ljl.framework.winter.aop.bean.AroundProxyBeanPostProcessor;
import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.boot.annotation.WinterBootApplication;
import com.github.ljl.framework.winter.context.context.ApplicationContext;
import com.github.ljl.framework.winter.test.aop.around.OriginBean;
import com.github.ljl.framework.winter.test.aop.service.IUserService;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 15:29
 **/

@WinterBootApplication
public class Main {
    String addUser = "INSERT INTO User (username, email, age) VALUES\n" +
            "('Alice', 'alice@example.com', 25);";


    public static void main(String[] args) {
        ApplicationContext applicationContext = WinterApplication.run(Main.class);
        OriginBean proxy = applicationContext.getBean(OriginBean.class);
        System.out.println(proxy.getClass().getName());
        System.out.println(proxy.hello());
        System.out.println(proxy.morning());

        IUserService userService = applicationContext.getBean(IUserService.class);
        // 应该被回滚
        userService.createUser("name", "what@aaaaaa.com", 114);
        userService.createUserWithInnerCall("name", "what@aaaaaa.com", 14);
        // 应该正常

    }
}
