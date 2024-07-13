## winter-framework prd 01

### socket编程实现HTTP请求获取与响应回复

| 版本号 | 发布日期       | 变更内容                                      | 负责人 |
|-----|------------|-------------------------------------------|-----|
| 1.1 | 2024-07-13 | 添加 [winter-prd-01.md](./winter-prd-01.md) | ljl |

### 1、项目背景
为学习spring对bean的管理，单例bean的不同注入方式，要求模仿spring，实现一个ioc容器


### 2、需求描述
#### 2.1 功能需求

手动实现IOC容器，不得使用任何spring库，只要求管理单例Bean，要求覆盖下列情况

- @ComponentScan(value=)标记扫描的范围，启动指定扫描类
- 范围内标记@Component表示单例Bean应该被容器管理
- A中有B，B中有A，能够正确注入
- 通过自定义的@Value注解，实现对application.yml中的字段的赋值
- 通过自定义@Autowired注解，以及java的@Resource注解注入Bean中的字段
- 自定义@Configuration 标记一个工厂，里面的@Bean标记工厂方法，能创建并注入容器
- 支持setter方法注入
- 要求实现@Primary标记：若有多个满足条件的bean要求注入，则注入@Primary标记的
- 要求构建BeanPostProcessor接口，在里面可以对构建的Bean进行修改


### 3、实现要求
#### 实现接口

在winter-context模块，实现如下接口，接口含义具体参考spring的实现
```java
/**
 * ioc容器，用于获取bean
 */
public interface ApplicationContext {

    boolean containsBean(String name);

    <T> T getBean(String name);

    <T> T getBean(String name, Class<T> requiredType);

    <T> T getBean(Class<T> requiredType);

    <T> List<T> getBeans(Class<T> requiredType);

    void close();
}

/**
 *  用于获取从.yml获取的属性以及系统变量
 */
public interface PropertyResolver {
    boolean containsProperty(String key);

    String getProperty(String key);

    String getProperty(String key, String defaultValue);

    <T> T getProperty(String key, Class<T> targetType);

    <T> T getProperty(String key, Class<T> targetType, T defaultValue);

    String getRequiredProperty(String key);

    <T> T getRequiredProperty(String key, Class<T> targetType);
}
```


#### 测试要求

编写测试用例，对 2.1的所有情况进行测试

#### 输出文档

- 输出本章节的经验总结文档

### 4、完成时间
预计用时 2 DAY，要求 `2024.7.13` 提交代码，文档`2024.8.31前`统一完成
