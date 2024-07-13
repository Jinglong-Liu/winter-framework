package com.github.ljl.framework.winter.boot.test.beans;

import com.github.ljl.framework.winter.context.annotation.Component;
import com.github.ljl.framework.winter.context.annotation.ComponentScan;
import com.github.ljl.framework.winter.context.annotation.Value;
import lombok.Data;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 21:00
 **/

@Data
@Component
public class Bean1 {
    @Value("${winter.port}")
    Integer port;
}
