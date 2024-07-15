package com.github.ljl.framework.winter.webmvc.config;

import com.github.ljl.framework.winter.context.annotation.Bean;
import com.github.ljl.framework.winter.context.annotation.Configuration;
import com.github.ljl.framework.winter.context.env.Environment;
import com.github.ljl.framework.winter.context.servlet.server.ServletContextInitializer;
import com.github.ljl.framework.winter.context.servlet.server.ServletWebServerFactory;
import com.github.ljl.framework.winter.context.servlet.server.WebServer;
import com.github.ljl.framework.winter.webmvc.servlet.DispatcherServlet;
import com.github.ljl.jerrymouse.bootstrap.JerryMouseBootStrap;
import com.github.ljl.jerrymouse.support.context.ApplicationContext;
import com.github.ljl.jerrymouse.support.servlet.ServletConfigWrapper;

import javax.annotation.Resource;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Objects;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-14 08:58
 **/

@Configuration
public class WebServerConfig {

    @Resource
    Environment environment;

    // 默认
    private static final String applicationName = "/root";

    @Bean
    ServletWebServerFactory servletFactory() {
        return initializers -> new JerryMouseWebServer(applicationName,  initializers);
    }

    class JerryMouseWebServer implements WebServer {
        private Integer PORT = 8080; //default
        private final Integer MAX_PORT = 65535;
        private final Integer MIN_PORT = 1024;

        public JerryMouseBootStrap server;
        private final ApplicationContext servletContext;
        public JerryMouseWebServer(String applicationName, ServletContextInitializer... initializers) {
            if (Objects.nonNull(environment)) {
                this.PORT = checkPort(environment.getProperty("server.port"));
            }
            this.server = new JerryMouseBootStrap();
            this.servletContext = server.getApplicationContext(applicationName);
            Arrays.stream(initializers).forEach(servletContextInitializer -> {
                try {
                    servletContextInitializer.onStartup(servletContext);
                } catch (ServletException e) {
                    e.printStackTrace();
                }
            });
        }
        @Override
        public void start() {
            // start 前，dispatcherServlet已经被winter mvc创建并注册好，key = dispatcherServlet
            final String dispatcherServletName = "dispatcherServlet";
            servletContext.log("jerry-mouse-servlet-web-server-factory start");
            try {
                Servlet servlet = servletContext.getServlet(dispatcherServletName);
                if (servlet instanceof DispatcherServlet) {
                    DispatcherServlet dispatcherServlet = (DispatcherServlet) servlet;
                    servletContext.registerServlet("/", dispatcherServlet);
                    ServletConfig servletConfig = new ServletConfigWrapper(servletContext, dispatcherServletName, servlet.getClass().getName());
                    dispatcherServlet.init(servletConfig);
                }
            } catch (ServletException e) {
                e.printStackTrace();
            }
            server.startBootApplication(PORT);
        }

        @Override
        public void stop() {
            server.stop();
        }

        @Override
        public int getPort() {
            return PORT;
        }

        private final Object lock = new Object();

        /**
         * @param object environment.getProperty("server.port")
         * @return valid port
         * number = Integer.parseInt(object)
         * 1024-65535: valid, return number
         * other/not set: 8080
         * conflict: next port(like 8081, 8082, ...)
         *
         * @throws IllegalStateException : not port Available
         */
        private Integer checkPort(Object object) {
            synchronized (lock) {
                int number = PORT;
                try {
                    number = Integer.parseInt((String) object);
                    if (number < MIN_PORT || number > MAX_PORT) {
                        number = PORT;
                    }
                } catch (NumberFormatException ignore) {
                    // invalid or not set port, using default 8080
                }
                while (number <= MAX_PORT && !isPortAvailable(number)) {
                    number++;
                }
                if (number > MAX_PORT) {
                    throw new IllegalStateException("All Ports from " + (object == null ? PORT : object) + " to " + MAX_PORT + " are not available!");
                }
                return number;
            }
        }
        private boolean isPortAvailable(int port) {
            try (ServerSocket ignored = new ServerSocket(port)) {
                return true;
            } catch (IOException e) {
                return false;
            }
        }
    }
}
