package com.github.ljl.framework.winter.context.test.bean;

import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.context.annotation.Value;
import com.github.ljl.framework.winter.context.test.service.ServiceC;
import lombok.Getter;
import lombok.Setter;

import javax.annotation.Resource;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 16:53
 **/

@Component
@Getter
@Setter
public class Bean1 {
    private String id;
    private User user;
    private ServiceC serviceC;

    @Autowired
    public void setUser(User user) {
        this.user = user;
    }

    @Resource
    public void setServiceC(ServiceC serviceC) {
        this.serviceC = serviceC;
    }

    @Value("${winter.bean1.id}")
    public void setId(String id) {
        this.id = id;
    }
}
