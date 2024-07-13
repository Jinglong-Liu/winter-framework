package com.github.ljl.framework.winter.boot;

import com.github.ljl.framework.winter.context.context.AnnotationConfigApplicationContext;
import com.github.ljl.framework.winter.context.context.ApplicationContext;
import com.github.ljl.framework.winter.context.io.PropertyResolver;
import com.github.ljl.framework.winter.context.io.StandardPropertyResolver;
import com.github.ljl.framework.winter.context.utils.YamlUtils;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 20:54
 **/

public class WinterApplication {

    private ApplicationContext applicationContext;

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
        Map<String, Object> configs = YamlUtils.loadYamlAsPlainMap("/application.yml");

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

        return this.applicationContext;
    }
}
