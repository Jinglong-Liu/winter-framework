package com.github.ljl.framework.winter.redis.core;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-16 17:17
 **/

public class JedisPoolConfigWrapper extends JedisPoolConfig {
    private HostAndPort hostAndPort;
    public JedisPoolConfigWrapper(HostAndPort originalHostAndPort) {
        super();
        this.hostAndPort = originalHostAndPort;
    }

    public HostAndPort getHostAndPort() {
        return hostAndPort;
    }
}
