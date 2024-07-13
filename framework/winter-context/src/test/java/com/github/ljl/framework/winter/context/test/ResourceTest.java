package com.github.ljl.framework.winter.context.test;

import com.github.ljl.framework.winter.context.context.AnnotationConfigApplicationContext;
import com.github.ljl.framework.winter.context.context.ApplicationContext;
import com.github.ljl.framework.winter.context.io.PropertyResolver;
import com.github.ljl.framework.winter.context.io.Resource;
import com.github.ljl.framework.winter.context.io.ResourceResolver;
import com.github.ljl.framework.winter.context.io.StandardPropertyResolver;
import com.github.ljl.framework.winter.context.test.bean.User;
import com.github.ljl.framework.winter.context.test.config.Config2;
import com.github.ljl.framework.winter.context.test.config.TestConfig;
import com.github.ljl.framework.winter.context.utils.YamlUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 11:11
 **/


public class ResourceTest {
    private static final Logger logger = LoggerFactory.getLogger(ResourceTest.class);
    @Test
    public void testResource() {
        ResourceResolver resourceResolver = new ResourceResolver("com.github.ljl.framework.winter.context.io");
        List<Object> list = resourceResolver.scan(resource -> resource);
        list.forEach(item -> {
            if (item instanceof Resource) {
                Resource resource = (Resource) item;
                logger.debug(resource.getName());
            }
        });
    }

    @Test
    void testComponentScan() throws FileNotFoundException {
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
        // 入参作为ComponentScan，扫到其他ComponentScan再扫别的
        ApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config2.class, propertyResolver);
        User user = applicationContext.getBean(User.class);
        Assertions.assertNotNull(user);
    }

}
