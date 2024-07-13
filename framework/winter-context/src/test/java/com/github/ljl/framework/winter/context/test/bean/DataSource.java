package com.github.ljl.framework.winter.context.test.bean;

import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.context.annotation.Value;
import com.github.ljl.framework.winter.context.test.service.ServiceB;
import lombok.Getter;

import javax.annotation.Resource;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 14:52
 **/

@Getter
@Component
public class DataSource {
    @Value("${winter.datasource.username}")
    private String username;
    @Value("${winter.datasource.password}")
    private String password;
    @Value("${winter.datasource.port:3306}")
    private Integer port;
    @Value("${winter.datasource.url:what url}")
    private String url;

    private final Integer ssh;

    private ServiceB serviceB;

    @Resource
    private Expr admin;

    public DataSource(@Value("${winter.ssh}") Integer ssh1) {
        this.ssh = ssh1;
    }

    @Autowired
    public void setServiceXXXX(ServiceB serviceB) {
        this.serviceB = serviceB;
    }
}
