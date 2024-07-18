package com.github.ljl.framework.winter.redis.connection.bean;

import com.github.ljl.framework.winter.context.annotation.Bean;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-18 13:33
 **/

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Info {
    String string1;
    Long long1;
    Integer integer1;
    String string2;
}
