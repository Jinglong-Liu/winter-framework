package com.github.ljl.framework.winter.boot;

import com.github.ljl.framework.winter.context.context.AnnotationConfigApplicationContext;
import com.github.ljl.framework.winter.context.context.ApplicationContext;
import com.github.ljl.framework.winter.context.exception.NoSuchBeanDefinitionException;
import com.github.ljl.framework.winter.context.io.PropertyResolver;
import com.github.ljl.framework.winter.context.io.StandardPropertyResolver;
import com.github.ljl.framework.winter.context.servlet.server.ServletContextInitializer;
import com.github.ljl.framework.winter.context.servlet.server.ServletWebServerFactory;
import com.github.ljl.framework.winter.context.servlet.server.WebServer;
import com.github.ljl.framework.winter.context.utils.YamlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 20:54
 **/

public class WinterApplication {

    private Logger logger = LoggerFactory.getLogger(WinterApplication.class);

    private ApplicationContext applicationContext;

    private ServletWebServerFactory webServerFactory;

    private ServletContext servletContext;

    public static ApplicationContext run(Class<?> clazz) {

        WinterApplication winterBootApplication = new WinterApplication();
        ApplicationContext context = null;
        try {
            context = winterBootApplication.runApplication(clazz);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return context;

    }
    private ApplicationContext runApplication(Class<?> clazz) throws FileNotFoundException {
        // yml
        Map<String, Object> configs =  YamlUtils.loadYamlAsPlainMap("/application.yml");

        if (configs.containsKey("winter.profiles.active")) {
            String version = (String) configs.get("winter.profiles.active");
            String yaml = "/application-" + version + ".yml";
            Map<String, Object> validConfig = YamlUtils.loadYamlAsPlainMap(yaml);
            configs.putAll(validConfig);
        }

        Properties props = new Properties();
        props.putAll(configs);

        PropertyResolver propertyResolver = new StandardPropertyResolver(props);

        this.applicationContext = new AnnotationConfigApplicationContext(clazz, propertyResolver);

        try {
            webServerFactory = applicationContext.getBean(ServletWebServerFactory.class);
            runWebServer();
        } catch (NoSuchBeanDefinitionException ignored) {
            logger.info("No ServletWebServerFactory found");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return applicationContext;
    }


    private void runWebServer() {
        final HttpServlet dispatcherServlet;
        String className = "com.github.ljl.framework.winter.webmvc.servlet.DispatcherServlet";
        try {
            dispatcherServlet = dispatcherServlet(className);
        } catch (Exception e) {
            logger.error("create dispatcherServlet error, target class name={}", className);
            e.printStackTrace();
            return;
        }
        List<ServletContextInitializer> mergedInitializers = new ArrayList<>();
        // set ServletContext to Application
        mergedInitializers.add(servletContext -> this.servletContext = servletContext);
        mergedInitializers.add(servletContext -> servletContext.addServlet("dispatcherServlet", dispatcherServlet));
        //

        ServletContextInitializer[] servletContextInitializer = mergedInitializers.toArray(new ServletContextInitializer[0]);
        WebServer webServer = webServerFactory.getWebServer(servletContextInitializer);

        if (Objects.isNull(webServer)) {
            throw new RuntimeException("webServer is null");
        }

        webServer.start();
    }
    private HttpServlet dispatcherServlet(String className) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?>[] parameterTypes = {ApplicationContext.class};
        Object[] args = new Object[]{applicationContext};
        Class<?> clazz = Class.forName(className);
        Constructor<?> constructor = clazz.getConstructor(parameterTypes);
        HttpServlet dispatcherServlet = (HttpServlet) constructor.newInstance(args);
        return dispatcherServlet;
    }
}
