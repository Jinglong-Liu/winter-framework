package com.github.ljl.framework.winter.context.test.bean;

import com.github.ljl.framework.winter.context.annotation.Bean;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 19:03
 **/

@Data
public class Bean4 {
    private static Logger logger = LoggerFactory.getLogger(Bean4.class);
    private String a;
    private String b;
    private String c;
    private String d;

    void init() {
        a = "init_bean4_a";
        b = "init_bean4_b";
    }
    void destroy() {
        a = "destroy_bean4_a";
        c = "destroy_bean4_c";
    }

    @Override
    public String toString() {
        return "Bean4{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", c='" + c + '\'' +
                ", d='" + d + '\'' +
                '}';
    }
}
