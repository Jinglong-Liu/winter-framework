package com.github.ljl.framework.winter.context.servlet.server;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 08:55
 **/

@FunctionalInterface
public interface ServletWebServerFactory {

    /**
     * Gets a new fully configured but paused {@link WebServer} instance. Clients should
     * not be able to connect to the returned server until {@link WebServer#start()} is
     * called (which happens when the {@code ApplicationContext} has been fully
     * refreshed).
     * @param initializers {@link ServletContextInitializer}s that should be applied as
     * the server starts
     * @return a fully configured and started {@link WebServer}
     * @see WebServer#stop()
     */
    WebServer getWebServer(ServletContextInitializer... initializers);

}
