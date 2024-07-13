package com.github.ljl.framework.winter.context.test.bean;

import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.context.annotation.Value;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 17:03
 **/

@Data
@Component

public class Bean2 {
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
        return "Bean2{" +
                "a='" + a + '\'' +
                ", b='" + b + '\'' +
                ", c='" + c + '\'' +
                ", d=" + d +
                '}';
    }

}
