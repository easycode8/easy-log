# easy-log
![Maven Central](https://img.shields.io/maven-central/v/io.github.easycode8/easy-log-core)

## 功能介绍

- 基于SpringBoot AOP实现：Easy-Log利用Spring AOP框架，通过定义切面来实现日志的记录和管理。
- 全自动记录日志：Easy-Log支持使用注解、自定义注解或者无注解的方式来记录操作日志。其中，支持Controller/Service免开发一键开启日志，让日志记录更加自动化和方便。
- 记录MyBatis执行操作的数据快照：Easy-Log支持无入侵自动记录MyBatis执行删除和修改操作的数据快照，方便用户在后续的数据修复及数据审计使用。
- 支持性能测试：Easy-Log提供了Stopwatch测试工具，可以方便地测试应用程序的性能。
- 扩展标签功能：Easy-Log支持使用自定义标签来扩展业务信息，方便用户进行日志的分类和查询。
- 支持异步记录日志：Easy-Log支持异步记录日志，可以避免日志记录对应用程序性能的影响。
- 在线管理日志功能：Easy-Log提供了Web管理界面，可以在线动态控制日志功能的启停、同步异步切换和日志处理器切换等功能。
- 扩展功能: Easy-Log标记日志/处理日志/控制日志,每个重要环节都保留丰富的扩展接口来满足业务个性化的需要。

## 快速开始
### 1.引入依赖
```xml
        <dependency>
            <groupId>io.github.easycode8</groupId>
            <artifactId>easy-log-spring-boot-starter</artifactId>
            <!-- 具体版本替换左上角最新版本号(数字部分)或者tag -->
            <version>latest</version>
        </dependency>
```

### 2.日志定义
> easy-log与传统日志注解标记业务,设计上有所不同。并不局限于指定某个注解增强,而是设计成一种开放能力。

#### 方式1: 无注解记录日志
无日志注解记录操作日志,适合只想简单记录审计日志,不想改动业务代码,包括加日志注解也不想改的”一行不改“,极致的解耦场景。

1. 开启扫描controller日志记录配置(service同样也是支持的)

```yaml
spring:
  easy-log:
    scan-controller:
      enabled: true #是否记录controller中的公开方法 默认:false
    scan-service:
      enabled: true #是否记录service中的公开方法 默认:false
```
2. 开发一个简单的接口

```java
@RequestMapping("/easy-log")
@Controller
public class TestLogController {

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(String username) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", username);
        return ResponseEntity.ok(result);
    }
}    
```

#### 方式2: @EasyLog记录日志
@EasyLog为框架提供默认日志注用于标记需要记录日志的controller/service等接口,并且实现了定义日志处理,异步处理,自定义标签属性等高级功能

常用注解属性解释

- value/title 为日志的标题,描述接口信息
- template 使用spel表达式用于解析提取请求参数中变量用于生产日志的业务描述

1. 开发一个简单的接口并使用@EasyLog标记

```java
@RequestMapping("/easy-log")
@Controller
public class TestLogController {

    @EasyLog(value = "list-查询列表信息", template = "'查询参数:' + #name ")
    @GetMapping("/list1")
    public ResponseEntity<Map<String, Object>> list1(String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", name);
        return ResponseEntity.ok(result);
    }

}
```
## 更多
详细入门教程/扩展用法/框架原理参考 [《easy-log使用教程》](https://easycode8.github.io/easy-log)


## 版本日志

### 1.0.6
1. 支持普通mybatis(接口以Dao或Mapper结尾)的数据快照记录
2. 优化日志属性缓存key获取,针对代理类对象。


### 1.0.5
1. 支持使用redis缓存动态日志配置--解决多实例配置同态同步修改
2. 日志配置缓存支持前缀key自定义配置

### 1.0.4
1. 修改easy-log-web模块使用纯静态资源easy-log-ui.html替换springmvc转发
2. 增加easy-log-web模块easy-log-ui.html在线访问鉴权 默认账号密码:admin/admin123
3. 去掉swagger默认note作为spel表达式逻辑, 修复解析报错
4. 文档补充

### 1.0.3
1. 增加easy-log-web模块支持web预览所有切面增强的日志点,支持动态启停日志/切换日志处理/同异步切换
2. 增加easy-log-data-mybatis-plus模块自动记录数据快照(任意mapper接口修改及删除操作)
### 1.0.2
1. 多线程中获取RequestContextHolder非空判断处理

### 1.0.1
1. 方法入参对象不可序列化兼容处理

### 1.0.0
1. 支持使用@EasyLog注解/自定义日志注解/无日志注解注标记记录操作日志-【注解不是必须的】
2. 支持配置session/header/请求参数/扩展接口实现获取操作人【前三种无需编码】
3. 支持SpEL表达式定义日志动态内容模板
4. 支持自定义标签Tags定义业务属性
5. 支持使用自定义对象扩展日志字段 【日志对象不再写死】
6. 支持不同日志处理器handler同时使用【不同日志不同处理】
7. 支持方法级日志处理策略handleMode【同步/异步/全局默认)】