package com.github.ljl.framework.winter.test.webmvc;

import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.jdbc.template.JdbcTemplate;
import com.github.ljl.framework.winter.webmvc.annotation.WinterBootWebApplication;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 11:15
 **/

@WinterBootWebApplication
public class WinterWebMvcTest {
    public static void main(String[] args) {
        WinterApplication.run(WinterWebMvcTest.class);
        RedisTemplate template;
        //GenericJackson2JsonRedisSerializer
        // template.execute()
        // template.expire()
        // RedisOperations1
        // RedisConnection
        // BitFieldSubCommands
    }
}
