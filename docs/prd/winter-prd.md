## winter-framework 需求文档

| 版本号 | 发布日期       | 变更内容                                      | 负责人 |
|-----|------------|-------------------------------------------|-----|
| 1.1 | 2024-07-13 | 添加 [winter-prd-01.md](./winter-prd-01.md) | ljl |
| 1.0 | 2024-07-13 | winter-framework prd初始版本                  | ljl |

### 1、项目背景

为深刻理解主流框架spring-framework的工作机制，计划开发一款名为winter-framework的轻量级框架，让程序员可以基于winter-framework，像基于spring-framework一样开发Java应用
且包含了winter-webmvc模块，可以借助之前实现的jerry-mouse服务器运行开发的Web服务端项目

### 2、目标

- 1、开发一个类似spring-framework的，名为 winter-framework的Java框架，能够进行基本的Bean对象管理和切面编程功能。
- 2、实现winter-webmvc模块，集成之前实现的jerry-mouse服务器，取代tomcat服务器，运行编写的app
- 3、实现winter-boot模块，让程序员像使用spring-boot一样轻松开发服务端项目
- 4、实现winter-jdbc模块，实现JdbcTemplate，封装对数据库的操作

### 3、功能需求

#### 3.1 winter-framework 基础功能

- 支持单例Bean对象的管理，实现ioc。
- 支持面向切面编程
- 支持Restful风格的webmvc
- 实现JdbcTemplate对SQL操作的封装

### 4、非功能需求

- 开发/运行环境：>= java 8
- 性能：winter-framework性能应该好于spring-framework
- 可用性：系统应具备高可用性，运行稳定，无明显崩溃。

### 5、接口要求

- context实现getBean接口，用于获取Bean
- jdbcTemplate实现query, execute接口，用于执行sql语句，查询结果
- 仿照springmvc的规范，实现@RestController, @GetMapping, @PostMapping, @Service等注解的功能

### 6、验收标准

- 1、winter-framework能够正确管理Bean对象以及配置，能够通过context获取Bean对象以及配置信息
- 2、封装的jdbcTemplate能够正确执行SQL语句
- 3、能够使用winter-boot，像使用spring-boot一样轻松开发服务端项目

### 7、时间表
- M1: 需求分析与设计（1 Day, 且持续完善）
- M2: winter-context 实现Bean和配置的管理 （1.5 Day）
- M3: 构建winter-boot 封装启动类 （0.5 Day）
- M4: 实现winter-webmvc （1 Day）
- M5: 实现基础的jdbcTemplate (1 Day)
- M6: 实现winter-aop (2 Day)
- M7: 在M5,M6基础上实现基本的事务管理 （1 Day）
- 
### 8、输出文档要求

#### 8.1 项目文档
- 需求文档

此文档。也包括各个版本迭代的prd

- 设计文档

各个阶段，各个模块的设计，必要的架构图，流程图

- 开发手册

详细描述winter-framework的开发流程，以及常见问题解决方案，给学习者以参考

#### 8.2 文档格式

- 所有文档以Markdown格式编写，可在Github上直接解析查看
- 必要的架构图，流程图同时保存.puml格式和.png格式
- 开发手册应在个人博文平台部署，且应面向没有看过spring源码，但是有初步使用过框架，或者写过jerry-mouse同学
- git commit 拆分合理，在github上体现，方便查看

### 9、附录

#### 9.1 spring-framework 源码

spring-framework: https://github.com/spring-projects/spring-framework

#### 9.1 现有的手写相关框架源码/教程

https://www.liaoxuefeng.com/wiki/1539348902182944 # 廖雪峰 仿写spring

https://github.com/Jinglong-Liu/jerry-mouse-round2 # 本人完成，仿写tomcat，会集成到winter-mvc。pr有详细过程

https://github.com/JasirVoriya/dark-one # 一款轻量级服务器框架，容易学习

#### 9.2 Spring官方网站

[spring-framework](https://spring.io/projects/spring-framework)

[spring-guides](https://spring.io/guides)

[spring-ioc](https://docs.spring.io/spring-framework/reference/core/beans.html)

[spring-aop](https://docs.spring.io/spring-framework/reference/core/aop.html)

[spring-jdbc](https://docs.spring.io/spring-framework/reference/data-access/jdbc.html)

[spring-webmvc](https://docs.spring.io/spring-framework/reference/web/webmvc.html)

#### 9.4 其他 Java 学习文档

[Lambda Expressions](https://docs.oracle.com/javase/tutorial/java/javaOO/lambdaexpressions.html)

[jdbc](https://docs.oracle.com/en/database/oracle/oracle-database/21/jjdbc/index.html)

[javamagazine](https://blogs.oracle.com/javamagazine/post/functional-programming-with-java-8)


#### 9.5 必要的工具安装

[git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git)

[jdk](https://www.oracle.com/java/technologies/downloads/)

[maven](https://maven.apache.org/install.html)
