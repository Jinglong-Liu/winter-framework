## winter-framework prd 02

### 启动基本封装与版本控制

| 版本号 | 发布日期       | 变更内容                                      | 负责人 |
|-----|------------|-------------------------------------------|-----|
| 1.4 | 2024-07-15 | 添加 [winter-prd-04.md](./winter-prd-04.md) | ljl |

### 1、项目背景

为了封装jdbc对数据库的操作，需要封装要给JdbcTemplate接口，方便用户执行SQL语句


### 2、需求描述
#### 2.1 功能需求

实现启动类注解`@WinterBootWebApplication`, 程序员可以通过以下方式，启动一个Winter-web项目, 无感知地开启一个Web服务器

自行构建ServerFactory，并进行封装，封装到winter-webmvc模块中，作为默认webServer工厂

这里的Web服务器选用之前自己实现的jerry-mouse, 而不是Tomcat

要求程序员可以像编写SpringBoot webapp那样丝滑地编写WinterBoot webapp

具体要求见实现接口

### 3、实现要求
#### 实现接口
在winter-jdbc模块实现接口JdbcTemplate, 要求如果用户加入了winter-jdbc的依赖，对应的bean会注册到winter-context中，可以直接获取
(通过自定义工厂方法的实现)
实现接口
```JAVA

public interface JdbcTemplate {

    /**
     * 执行SQL语句，包括update, delete, insert, create等都可以
     * @param sql
     * @param args 可选条件
     */
    void execute(String sql, Object... args);

    /**
     * 查询
     * @param sql sql
     * @param rowMapper 自定义映射关系
     * @param args 条件
     * @param <T> 预期返回类型
     * @return
     */
    <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;

    /**
     * @param sql sql
     * @param elementType 预计返回的类型, 字段对应，自动生成RowMapper
     * @param args 条件
     * @param <T>
     * @return
     */
    <T> T queryForObject(String sql, Class<T> elementType, Object... args) throws DataAccessException;

    /**
     * @param sql sql
     * @param elementType 预计返回的类型，字段对应，自动生成RowMapper
     * @param args 条件
     * @param <T>
     * @return
     */
    <T> List<T> queryForList(String sql, Class<T> elementType, Object... args) throws DataAccessException;

    /**
     * @param sql sql
     * @param rowMapper 自定义映射关系
     * @param args 条件
     * @param <T>
     * @return
     */
    <T> List<T> queryForList(String sql, RowMapper<T> rowMapper, Object... args) throws DataAccessException;
}
```
使用方式
```XML
<dependency>
    <groupId>com.github</groupId>
    <artifactId>winter-jdbc</artifactId>
    <version>1.0-SNAPSHOT</version>
</dependency>
```

```java
@Service
public class UserService {

    @Resource
    private JdbcTemplate jdbcTemplate;
}
```


连接信息：程序员可以在yml里面配置，格式如下(例子)
```YML
winter:
  datasource:
    url: jdbc:mysql://localhost:3306/winter?useSSL=false&autoReconnect=true&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8
    driver-class-name: com.mysql.cj.jdbc.Driver
    username: root
    password: root
```

事务管理：暂不要求

#### 测试用例
winter-testcase编写测试用例，尽量覆盖上述情况

分别编写直接使用jdbc的api和使用template的api，作为对比


#### 输出文档
详细说明各个参数类型，注解的含义和作用，映射规约

### 3、完成时间
预计用时 1.5 DAY，要求 `2024.7.15` 提交代码，测试和文档`2024.8.31前`统一完成
