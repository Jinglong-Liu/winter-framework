package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.context.annotation.Autowired;
import com.github.ljl.framework.winter.redis.core.JedisPoolConfigWrapper;
import lombok.Getter;
import lombok.Setter;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.JedisPoolConfig;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 18:41
 **/

public class JedisConnectionPool implements RedisConnectionPool {
    private final BlockingQueue<JedisConnection> pool;

    /**
     * 自动注入
     */
    @Autowired
    private JedisClientConfig jedisClientConfig;

    @Getter
    private JedisPoolConfigWrapper jedisPoolConfig;

    private HostAndPort clientHostAndPort;

    public JedisConnectionPool(JedisPoolConfigWrapper jedisPoolConfig, JedisClientConfig jedisClientConfig) {
        this.pool = new ArrayBlockingQueue<>(jedisPoolConfig.getMaxTotal());
        this.jedisPoolConfig = jedisPoolConfig;
        this.jedisClientConfig = jedisClientConfig;
        // 入参->map成新的hostAndPort，map在client中设置，当然单机这里入参无意义
        clientHostAndPort = jedisClientConfig.getHostAndPortMapper().getHostAndPort(jedisPoolConfig.getHostAndPort());
    }

    @Override
    public RedisConnection getConnection() throws InterruptedException {
        JedisConnection connection = pool.poll();
        if (connection == null && pool.size() < jedisPoolConfig.getMaxTotal()) {
            connection = createNewConnection(clientHostAndPort);
        }
        return (RedisConnection) (connection != null ? connection : pool.take());
    }

    @Override
    public void returnConnection(RedisConnection connection) {
        if (connection != null) {
            pool.offer((JedisConnection) connection);
        }
    }

    @Override
    public void close() {
        pool.forEach(JedisConnection::disconnect);
    }

    private JedisConnection createNewConnection(HostAndPort  hostAndPort) {
        JedisConnection connection = new JedisConnection(hostAndPort, jedisClientConfig);
        connection.connect();
        return connection;
    }
}
