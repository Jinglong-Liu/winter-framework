package com.github.ljl.framework.winter.redis.connection;

import com.github.ljl.framework.winter.redis.exception.ConvertJedisAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisClientConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-15 18:43
 **/

public class JedisConnection extends AbstractRedisConnectionDelegate implements RedisConnection {

    private static final Logger logger = LoggerFactory.getLogger(JedisConnection.class);

    private Jedis jedis;

    private Integer id;

    private final HostAndPort hostAndPort;

    private final JedisClientConfig jedisClientConfig;

    private final JedisKeyCommands jedisKeyCommands = new JedisKeyCommands(this);

    private final JedisStringCommands jedisStringCommands = new JedisStringCommands(this);

    private final JedisHashCommands jedisHashCommands = new JedisHashCommands(this);

    public JedisConnection(HostAndPort hostAndPort, JedisClientConfig jedisClientConfig) {
        this.hostAndPort = hostAndPort;
        this.jedisClientConfig = jedisClientConfig;
    }

    @Override
    public RedisKeyCommands keyCommands() {
        return jedisKeyCommands;
    }

    @Override
    public RedisStringCommands stringCommands() {
        return jedisStringCommands;
    }

    @Override
    public RedisHashCommands hashCommands() {
        return jedisHashCommands;
    }

    public Jedis getJedis() {
        return jedis;
    }

    public JedisInvoker invoke() {
        return new JedisInvoker((callFunction, converter, nullDefault) -> doInvoke(callFunction, converter, nullDefault));
    }

    // 真正创建jedis客户端，传入host, port, config
    public boolean connect() {
        jedis = new Jedis(hostAndPort, jedisClientConfig);
        return isConnected(jedis);
    }

    public void disconnect() {
        logger.debug("JedisConnection " + id + " disconnected.");
        jedis.disconnect();
    }

    public int getId() {
        return id;
    }
    public boolean isConnected(Jedis jedis) {
        try {
            if ("PONG".equals(jedis.ping())) {
                logger.debug("JedisConnection " + id + " connected.");
                return true;
            }
        } catch (JedisConnectionException e) {
            return false;
        }
        return false;
    }

    /**
     * @param directFunction 从jedis 到 result 的函数
     * @param converter 对结果的转化，一般 element -> element
     * @param nullDefault 如果空，默认值的封装，一般可以为null
     * @return
     */
    private Object doInvoke(Function<Jedis, Object> directFunction,
                            Converter<Object, Object> converter,
                            Supplier<Object> nullDefault) {

        return doWithJedis(it -> {
            Object result = directFunction.apply(getJedis());
            if (result == null) {
                return nullDefault.get();
            }
            return converter.convert(result);
        });
    }

    /**
     * 这里getJedis()真正引入连接对应的jedis客户端
     * @param callback
     * @param <T>
     * @return
     */
    private <T> T doWithJedis(Function<Jedis, T> callback) {
        try {
            // callback是jedis -> T
            return callback.apply(getJedis());

        } catch (Exception ex) {
            throw new ConvertJedisAccessException(ex);
        }
    }

}
