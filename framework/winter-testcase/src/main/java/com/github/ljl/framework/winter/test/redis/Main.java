package com.github.ljl.framework.winter.test.redis;

import com.github.ljl.framework.winter.boot.WinterApplication;
import com.github.ljl.framework.winter.boot.annotation.WinterBootApplication;
import com.github.ljl.framework.winter.context.context.ApplicationContext;
import com.github.ljl.framework.winter.redis.template.RedisTemplate;
import redis.clients.jedis.Jedis;;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 12:15
 **/

@WinterBootApplication
public class Main {
    public static void main(String[] args) {
        Jedis jedis = new Jedis("localhost",6379);
        final ApplicationContext context = WinterApplication.run(Main.class);
        RedisTemplate template = context.getBean(RedisTemplate.class);
        assert template != null;
    }
}
