package com.github.ljl.framework.winter.context.test.bean;

import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.context.annotation.Value;
import lombok.Data;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 18:50
 **/

@Component
@Data
public class Bean3 {
    private static Logger logger = LoggerFactory.getLogger(Bean3.class);
    @Value("${winter.bean2.a}")
    String a;
    @Value("${winter.bean2.b}")
    String b;
    @Value("${winter.bean2.c}")
    String c;
    @Value("${winter.bean2.d}")
    String d;

    @Override
    public String toString() {
        return "Bean3{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", c='" + c + '\'' +
                ", d='" + d + '\'' +
                '}';
    }

    @PostConstruct
    void postInit() {
        a = "post_init_a";
        b = "post_init_b";
        c = "post_init_c";
        logger.debug("bean3_post_init");
    }

    void init() {
        a = "init_a";
        b = "init_b";
    }

    void destroy() {
        a = "destroy_a";
        d = "destroy_d";
    }

    @PreDestroy
    void preDestroy() {
        a = "pre_Destroy_a";
        logger.debug("bean3_pre_destroy");
    }
}
