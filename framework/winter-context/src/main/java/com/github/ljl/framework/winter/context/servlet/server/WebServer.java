package com.github.ljl.framework.winter.context.servlet.server;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 08:55
 **/

public interface WebServer {
    void start();
    void stop();
    int getPort();
}
