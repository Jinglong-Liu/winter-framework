package com.github.ljl.framework.winter.context.test;

import com.github.ljl.framework.winter.context.annotation.Bean;
import com.github.ljl.framework.winter.context.context.AnnotationConfigApplicationContext;
import com.github.ljl.framework.winter.context.context.ApplicationContext;
import com.github.ljl.framework.winter.context.env.Environment;
import com.github.ljl.framework.winter.context.exception.NoSuchBeanDefinitionException;
import com.github.ljl.framework.winter.context.io.PropertyResolver;
import com.github.ljl.framework.winter.context.io.StandardPropertyResolver;
import com.github.ljl.framework.winter.context.test.bean.*;
import com.github.ljl.framework.winter.context.test.config.TestConfig;
import com.github.ljl.framework.winter.context.test.service.ServiceA;
import com.github.ljl.framework.winter.context.test.service.ServiceB;
import com.github.ljl.framework.winter.context.test.service.ServiceC;
import com.github.ljl.framework.winter.context.utils.YamlUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @program: winter-framework
 * @description:
 * @author: ljl
 * @create: 2024-07-13 13:44
 **/

public class ApplicationContextTest {

    private static ApplicationContext applicationContext;
    private static PropertyResolver propertyResolver;
    @BeforeAll
    static void createContext() throws FileNotFoundException {
        Map<String, Object> configs = YamlUtils.loadYamlAsPlainMap("/application.yml");

        if (configs.containsKey("winter.profiles.active")) {
            String version = (String) configs.get("winter.profiles.active");
            String yaml = "/application-" + version + ".yml";
            Map<String, Object> validConfig = YamlUtils.loadYamlAsPlainMap(yaml);
            configs.putAll(validConfig);
        }
        Properties props = new Properties();
        props.putAll(configs);
        propertyResolver = new StandardPropertyResolver(props);
        // 入参作为ComponentScan，扫到其他ComponentScan再扫别的
        applicationContext = new AnnotationConfigApplicationContext(TestConfig.class, propertyResolver);
    }

    @Test
    void testValueAndEnvironment() {
        Environment environment = applicationContext.getBean(Environment.class);

        Assertions.assertEquals("1.0", environment.getProperty("winter.app.version"));
        Assertions.assertEquals("winter-app", environment.getProperty("winter.app.name"));
        Assertions.assertEquals(2333, environment.getProperty("winter.app.port", Integer.class, 6666));
        Assertions.assertEquals("root", environment.getProperty("winter.datasource.username", String.class));
        Assertions.assertEquals(20, environment.getProperty("temperature", Integer.class, 20));
        Assertions.assertTrue(environment.containsBean("bean1"));
        Assertions.assertFalse(environment.containsBean("temperature"));
        DataSource dataSource = environment.getBean(DataSource.class);

        Assertions.assertEquals("root", dataSource.getUsername());
        Assertions.assertEquals("pswd", dataSource.getPassword());
        Assertions.assertEquals(3307, dataSource.getPort());
        Assertions.assertEquals("what url", dataSource.getUrl());
        Assertions.assertEquals(22, dataSource.getSsh());
        Assertions.assertEquals(environment.getBean(ServiceB.class), dataSource.getServiceB());



    }

    @Test
    void testBeanPostProcessor() {
        User user = applicationContext.getBean(User.class);

        Assertions.assertEquals("user_id", user.getId());
        Assertions.assertEquals("user_name", user.getName());
        Assertions.assertEquals("10000@edf.com", user.getEmail());
        Assertions.assertEquals("30", user.getAge());

        Environment environment = applicationContext.getBean(Environment.class);
        Assertions.assertTrue(environment.containsBean("bean2"));
        Bean2 bean2 = environment.getBean("bean2", Bean2.class);
        Assertions.assertEquals("a", bean2.getA());
        Assertions.assertEquals("bb", bean2.getB());
        Assertions.assertEquals("cc", bean2.getC());
        Assertions.assertEquals("dd", bean2.getD());
    }

    @Test
    void testFactoryBeanResource() {
        Expr admin = applicationContext.getBean(Expr.class);
        DataSource dataSource = applicationContext.getBean("dataSource");
        Assertions.assertEquals(admin, dataSource.getAdmin());
        Assertions.assertEquals("Administer:admin,root", admin.expr());

        User user = applicationContext.getBean(User.class);
        Bean1 bean1 = applicationContext.getBeans(Bean1.class).get(0);
        Assertions.assertEquals(bean1.getUser(), user);
        Assertions.assertEquals(bean1.getId(), "bean1_id");
    }

    @Test
    void testCircularDependency() {
        ServiceA serviceA = applicationContext.getBean(ServiceA.class);
        ServiceB serviceB = applicationContext.getBean("serviceB");
        ServiceC serviceC = applicationContext.getBean("serviceC", ServiceC.class);
        Assertions.assertEquals(serviceA, serviceB.getServiceC());
        Assertions.assertEquals(serviceB, serviceA.getServiceB());
        Assertions.assertEquals(serviceC.getServiceA(), serviceA);
        Assertions.assertEquals(serviceC.getServiceD(), serviceA);
        Assertions.assertEquals(serviceC.getServiceX(), serviceB);
        Assertions.assertEquals(serviceA.getServiceC(), serviceC);
    }

    @Test
    void testException() {
        Environment environment = applicationContext.getBean("environment", Environment.class);
        Assertions.assertThrows(NoSuchBeanDefinitionException.class, ()-> environment.getBean(Objects.class));
        Assertions.assertThrows(NullPointerException.class, () -> environment.getRequiredProperty("abc"));
    }

    @Test
    void testPostConstructAndPreDestroy() {
        Bean3 bean3 = applicationContext.getBeans(Bean3.class).get(0);
        Assertions.assertEquals("post_init_a", bean3.getA());
        applicationContext.close();
        Assertions.assertEquals("pre_Destroy_a", bean3.getA());
        applicationContext = new AnnotationConfigApplicationContext(TestConfig.class, propertyResolver);
    }

    @Test
    void testInitAndDestroy() {
        Bean4 bean4 = applicationContext.getBeans(Bean4.class).get(0);
        Assertions.assertEquals("Bean4{a='init_bean4_a', b='init_bean4_b', c='create_bean4_c', d='create_bean4_d'}", bean4.toString());
        applicationContext.close();
        Assertions.assertEquals("Bean4{a='destroy_bean4_a', b='init_bean4_b', c='destroy_bean4_c', d='create_bean4_d'}", bean4.toString());
        //Assertions.assertEquals("pre_Destroy_a", bean3.getA());
        applicationContext = new AnnotationConfigApplicationContext(TestConfig.class, propertyResolver);
    }
}
