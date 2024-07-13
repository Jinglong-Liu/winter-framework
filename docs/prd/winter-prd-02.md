## winter-framework prd 02

### 启动基本封装与版本控制

| 版本号 | 发布日期       | 变更内容                                      | 负责人 |
|-----|------------|-------------------------------------------|-----|
| 1.2 | 2024-07-13 | 添加 [winter-prd-02.md](./winter-prd-02.md) | ljl |

### 1、项目背景

为方便用于启动程序，以及后续的测试要求，需要仿照SpringBoot实现一个Winter Boot, 用户可以快速启动一个程序，不需要自己封装

为方便测试，可能存在多个版本的.yml，现要求按需要解析。

暂时不需要支持web-server

### 2、需求描述
#### 2.1 功能需求

1、封装前面的Resolver和Application创建的过程，让用户能像SpringBoot一样，快速启动一个容器。 暂时不需要带webserver

2、要求简单的版本控制：可能存在多个xml，格式为application-<value>.xml

解析规则如下：

启动时直接解析application.yml

若发现winter.profiles.active 有值<value>，则使用application-<value>的配置

不然，使用application.yml本身的配置

### 3、实现要求
#### 实现接口

1、在winter-boot模块，实现接口和注解
```java
public class WinterApplication {
    public static ApplicationContext run(Class<?> clazz) {}
}

public @interface WinterBootApplication {
}

```

让用户可以通过这样的方式直接启动一个容器。扫描入参(下面就是ApplicationTest.class) 所在包(带上@WinterBootApplication)

```java
@WinterBootApplication
public class ApplicationTest {
    public static void main(String[] args) {
        ApplicationContext applicationContext = WinterApplication.run(ApplicationTest.class);
    }
}
```

ApplicationContext功能与前一版本相同即可。


2、解析yml要求：

启动时直接解析application.yml

若发现winter.profiles.active 有值<value>，则使用application-<value>的配置

不然，使用application.yml本身的配置

### 3、完成时间
预计用时 0.5 DAY，要求 `2024.7.13` 提交代码，文档`2024.8.31前`统一完成
