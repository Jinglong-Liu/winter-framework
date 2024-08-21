package com.github.ljl.framework.winter.redis.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.context.annotation.Bean;
import com.github.ljl.framework.winter.context.annotation.Configuration;
import com.github.ljl.framework.winter.context.annotation.Value;
import com.github.ljl.framework.winter.redis.connection.JedisConnectionPool;
import com.github.ljl.framework.winter.redis.connection.RedisConnectionPool;
import com.github.ljl.framework.winter.redis.core.JedisPoolConfigWrapper;
import com.github.ljl.framework.winter.redis.serializer.Jackson2JsonRedisSerializer;
import com.github.ljl.framework.winter.redis.serializer.StringRedisSerializer;
import com.github.ljl.framework.winter.redis.template.RedisTemplate;
import redis.clients.jedis.*;

import java.time.Duration;
import java.util.Map;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 18:44
 **/

@Configuration
public class RedisConnectionPoolFactory {

    @Bean
    public RedisConnectionPool redisConnectionPool(
         @Autowired JedisClientConfig jedisClientConfig,
         @Value("${winter.redis.max-wait:1000}") Long maxWait,
         @Value("${winter.redis.jedis.max-active:8}") Integer maxActive,
         @Value("${winter.redis.jedis.max-idle:8}") Integer maxIdle,
         @Value("${winter.redis.jedis.min-idle:0}") Integer minIdle,
         @Value("${winter.redis.host:localhost}") String host,
         @Value("${winter.redis.port:6379}") Integer port
    ) {
        JedisPoolConfigWrapper jedisPoolConfig = new JedisPoolConfigWrapper(new HostAndPort(host, port));
        jedisPoolConfig.setMaxTotal(maxActive);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setMaxWait(Duration.ofMillis(maxWait));
        return new JedisConnectionPool(jedisPoolConfig, jedisClientConfig);
    }

    @Bean
    public JedisClientConfig jedisConfig(@Value("${winter.redis.host:localhost}") String host,
                                         @Value("${winter.redis.port:6379}") Integer port,
                                         @Value("${winter.redis.user}") String user,
                                         @Value("${winter.redis.password}") String password,
                                         @Value("${winter.redis.database:0}") Integer database, // 一共0-15，共16个数据库
                                         @Value("${winter.redis.timeout:2000}") Integer connectionTimeoutMillis,
                                         @Value("${winter.redis.jedis-client.clientName:}") String clientName,
                                         @Value("${winter.redis.jedis-client.socket-timeout:2000}") Integer socketTimeoutMillis,
                                         @Value("${winter.redis.jedis-client.blocking-socket-timeout:2000}") Integer blockingSocketTimeoutMillis) {

        return DefaultJedisClientConfig.builder()
                .hostAndPortMapper((hostAndPort) -> new HostAndPort(host, port))
//                .user(user)
//                .password(password)
                .database(database)
                .clientName(clientName)
                .connectionTimeoutMillis(connectionTimeoutMillis)
                .socketTimeoutMillis(socketTimeoutMillis)
                .blockingSocketTimeoutMillis(blockingSocketTimeoutMillis)
                .build();
    }

    @Bean(value = "stringStringRedisTemplate")
    public RedisTemplate<String, String> stringStringRedisTemplate() {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        return template;
    }

    @Bean(value = "stringObjectRedisTemplate")
    public RedisTemplate<String, Object> stringObjectRedisTemplate() {
        RedisTemplate<String, Object> template = new RedisTemplate<>();

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = StringRedisSerializer.get();
        template.setKeySerializer(stringRedisSerializer);

        // 使用jackson2JsonRedisSerializer来序列化和反序列化redis的value值
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer();
        template.setValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }
    @Bean(value = "redisTemplate")
    public RedisTemplate objectRedisTemplate() {
        RedisTemplate template = new RedisTemplate<>();

        // 使用jackson2JsonRedisSerializer来序列化和反序列化redis的 key 和 value 值
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer();
        template.setKeySerializer(jackson2JsonRedisSerializer);
        template.setValueSerializer(jackson2JsonRedisSerializer);

        return template;
    }

    @Bean(value = "hashStringRedisTemplate")
    public RedisTemplate<String, Map<String, String>> hashStringRedisTemplate() {
        RedisTemplate<String, Map<String, String>> template = new RedisTemplate<>();

        // 使用StringRedisSerializer来序列化和反序列化redis的key值
        StringRedisSerializer stringRedisSerializer = StringRedisSerializer.get();
        template.setKeySerializer(stringRedisSerializer);
        template.setHashKeySerializer(stringRedisSerializer);

        // 使用StringRedisSerializer来序列化和反序列化redis的value值
        // value: (key, field) -> value
        template.setValueSerializer(stringRedisSerializer);
        template.setHashValueSerializer(stringRedisSerializer);

        return template;
    }
}
