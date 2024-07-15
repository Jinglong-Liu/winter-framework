## winter-framework prd 02

### 启动基本封装与版本控制

| 版本号 | 发布日期       | 变更内容                                      | 负责人 |
|-----|------------|-------------------------------------------|-----|
| 1.3 | 2024-07-15 | 添加 [winter-prd-03.md](./winter-prd-03.md) | ljl |

### 1、项目背景

为了实现webmvc基本功能，用户可以像使用SpringBoot一样使用WinterBoot, 启动一个内置的Web服务器，并使用@RestController进行开发
需要实现winter-webmvc模块的部分功能


### 2、需求描述
#### 2.1 功能需求

实现启动类注解`@WinterBootWebApplication`, 程序员可以通过以下方式，启动一个Winter-web项目, 无感知地开启一个Web服务器

自行构建ServerFactory，并进行封装，封装到winter-webmvc模块中，作为默认webServer工厂

这里的Web服务器选用之前自己实现的jerry-mouse, 而不是Tomcat

要求程序员可以像编写SpringBoot webapp那样丝滑地编写WinterBoot webapp

具体要求见实现接口

### 3、实现要求
#### 实现接口

#### 1、在winter-webmvc模块，实现注解`@WinterBootWebApplication`，让程序员可以丝滑地启动webapp
```java
@WinterBootWebApplication
public class WinterWebMvcTest {
    public static void main(String[] args) {
        WinterApplication.run(WinterWebMvcTest.class);
    }
}
```
可以在application.yml中，server.port字段指定端口，默认8080，冲突则寻找下一个可用端口

#### 2、要求程序员可以使用以下Web相关的注解, 编写后端接口，返回json串/字符串。无需支持jsp

##### 2.1 Http Request Method

目前只要求GET和POST，实现对应的@GetMapping和@PostMapping

##### 2.2 Controller参数要求
支持以下类型的参数，可以根据request正确解析：

| 注解（自定义）           | 类型(含子类)             | 要求                                      | 其他  |
|-------------------|---------------------|-----------------------------------------|-----|
| /                 | HttpServletRequest  |                                         |     |
| /                 | HttpServletResponse |                                         |     |
| /                 | HttpServletSession  | 没有则是null，不自动创建，不报错                      |     |
| /                 | Reader              | 和request关联的Reader                       |     |
| /                 | InputStream         | 和request关联的InputStream                  |     |
| /                 | Writer              | 和Response关联的Writer                      |     |
| /                 | OutputStream        | 和Response关联的OutputStream                |     |
| @RequestParam     | String              | 要求value和request的Param对应，无顺序要求           |     |
| @RequestBody      | T / Map             | 要求Body是json类型，可以转化为Map或者指定类型            |     |
| @PathVariable     | String              | 在url中拼接路径，要求第一次{}之前前缀匹配，出现之后完全匹配，支持多个{} |     |
| @RequestAttribute | Object              | /                                       |     |
| @SessionAttribute | Object              | /                                       |     |
| 其他                | 其他                  | 报错                                      |     |

暂时不要求匹配**等模糊匹配的情况

##### 2.3 Controller和返回要求

| Controller类型    | 返回类型(Body)                        | 要求        | 
|-----------------|-----------------------------------|-----------|
| @RestController | T (Object)                        | 解析成json返回 |
| /               | Map                               | 解析成json返回 |
| /               | String                            | 直接返回      |
| /               | Byte[],byte[]                     | 直接返回      |
| /               | ResponseEntity<T>(winter-mvc框架提供) | 解析并返回     |
| Controller      | 不支持                               | 不支持       |

状态码：200，404，500（jerry-mouse支持的状态码）

##### 2.4 @Service
要求提供@Service，加在字段上，可以用@Autowired或者@Resource注入单例Service

#### 测试用例
winter-testcase编写测试用例，尽量覆盖上述情况

#### 输出文档
详细说明各个参数类型，注解的含义和作用，映射规约

### 3、完成时间
预计用时 2.5 DAY，要求 `2024.7.15` 提交代码，测试和文档`2024.8.31前`统一完成
