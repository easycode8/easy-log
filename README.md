# easy-log
![Maven Central](https://img.shields.io/maven-central/v/io.github.easycode8/easy-log-core)

- [研发工作的痛点](#研发工作的痛点)
- [功能介绍](#功能介绍)
- [快速开始](#快速开始)
    - [1.引入依赖](#1引入依赖)
    - [2.开启自动日志记录](#2开启自动日志记录)
    - [3.配置操作人](#3配置操作人)
    - [4.动态修改日志配置及在线日志](#4动态修改日志配置及在线日志)
    - [5.开启数据快照功能](#5开启数据快照功能)
- [更多使用说明](#更多使用说明)
- [版本日志](#版本日志)
    - [1.0.12](#1012)
    - [1.0.11](#1011)
    - [1.0.10](#1010)


## 研发工作的痛点
1. 开发一个新业务项目,发现没有系统日志基础设施,要迁移aop日志代码,或者每次只能用一个模板项目全套依赖。
2. 接手一个旧业务项目,发现都是业务代码,很多关键代码没有日志输出,排查问题困难,补日志痛苦。
3. 使用日志采集统一收集日志,发现没有请求traceId来唯一标识相关业务日志,定位问题难。
4. 使用日志记录保存到数据库,有时候会导致数据量过大,不能灵活控制日志处理的开关。
5. 系统关键配置被用户误修改或者误删除, 导致业务功能异常, 没法排查谁改了,改之前是什么数据,没有快照数据。

为了解决以上痛点,开发了easy-log日志框架。

## 功能介绍

- 基于SpringBoot AOP实现：Easy-Log利用Spring AOP框架，通过定义切面来实现日志抽象管理和记录。
- 全自动记录日志：Easy-Log支持使用注解、自定义注解或者无注解的方式来记录操作日志。其中，支持Controller/Service免开发一键开启日志，让日志记录更加自动化和方便。
- 记录MyBatis执行操作的数据快照：Easy-Log支持无入侵自动记录MyBatis执行删除和修改操作的数据快照，方便用户在后续的数据修复及数据审计使用。
- 支持性能测试：Easy-Log提供了Stopwatch测试工具，可以方便地测试应用程序的性能。
- 扩展标签功能：Easy-Log支持使用自定义标签来扩展业务信息，方便用户进行日志的分类和查询。
- 支持异步记录日志：Easy-Log支持异步记录日志，可以避免日志记录对应用程序性能的影响。
- 在线管理日志功能：Easy-Log提供了Web管理界面，可以在线动态控制日志功能的启停、同步异步切换和日志处理器切换等功能。
- 在线实时日志查看：通过Web管理页面，可实时查看控制台日志。  
- 扩展功能: Easy-Log标记日志/处理日志/控制日志,每个重要环节都保留丰富的扩展接口来满足业务个性化的需要。
- 日志追踪功能: Easy-Log提供默认traceId日志追踪能力,同时支持集成spring的sleuth/zipkin。

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

### 2.开启自动日志记录
开启扫描controller/service 日志
```yaml

spring:
  application:
    name: sample-service #你服务的名称
  easy-log:
    scan-controller:
      enabled: true #是否记录controller中的公开方法 默认:false
    scan-service:
      enabled: true #是否记录service中的公开方法 默认:false
```
启动项目,调用web接口
```markdown
2023-08-11 10:25:42.615  INFO [sample-service,478227a216a34dc2954d6ff714550df5] 37360 --- [nio-8000-exec-8] c.e.e.core.handler.DefaultLogHandler     : [easy-log][TestLogController.list(username)]--begin operator:[null] param:username=aaaaaaa
2023-08-11 10:25:42.618  INFO [sample-service,478227a216a34dc2954d6ff714550df5] 37360 --- [nio-8000-exec-8] c.e.e.core.handler.DefaultLogHandler     : [easy-log][TestLogController.list(username)]--end timeout:5 
```
- 自动记录请求类及方法,并记录参数及超时时间
- 自动为请求生成traceId

如果要替换原始类及方法日志信息,支持日志注解方式@EasyLog 

> @EasyLog 支持请求参数spel解析,定义日志处理,异步处理,自定义标签属性等高级功能请参考[《easy-log使用教程》](https://easycode8.github.io/easy-log)

```java
    @EasyLog(value = "list-查询列表信息", template = "'查询参数:' + #name ")
    @GetMapping("/list1")
    public ResponseEntity<Map<String, Object>> list1(String name) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", name);
        return ResponseEntity.ok(result);
    }

```
效果
```markdown
2023-08-11 10:34:35.722  INFO [sample-service,873fcfd33b4f4f02a3124e8c0fcda490] 37360 --- [nio-8000-exec-6] c.e.e.core.handler.DefaultLogHandler     : [easy-log][list-查询列表信息]--begin operator:[null] param:name=zhangsan
2023-08-11 10:34:35.722  INFO [sample-service,873fcfd33b4f4f02a3124e8c0fcda490] 37360 --- [nio-8000-exec-6] c.e.e.core.handler.DefaultLogHandler     : [easy-log][list-查询列表信息]--end timeout:0 
```
如果项目集成了swagger/knife4j的在线接口文档已经有定义. 注解也不用写. 开启swagger接口记录日志(同时使用@EasyLog, @EasyLog的优先级更高)
```yaml
spring:
  application:
    name: sample-service
  easy-log:
    scan-swagger:
      enabled: true #是否将swagger的@ApiOperation接口注解标识作为日志记录 默认:false
```
swagger接口描述
```java
    @ApiOperation("测试post提交json数据")
    @PostMapping
    public ResponseEntity<Map<String, Object>> testPost(@RequestBody AccountParam param) {
        Map<String, Object> result = new HashMap<>();
        result.put("data", param);
        return ResponseEntity.ok(result);
    }
```
效果
```markdown
2023-08-11 11:00:36.332  INFO [sample-service,8bfca421c1ce4dc196c120026a60c0eb] 39192 --- [nio-8000-exec-1] c.e.e.core.handler.DefaultLogHandler     : [easy-log][测试post提交json数据]--begin operator:[null] param:{"id":10086,"name":"张三","username":"zhangsan"}
2023-08-11 11:00:36.335  INFO [sample-service,8bfca421c1ce4dc196c120026a60c0eb] 39192 --- [nio-8000-exec-1] c.e.e.core.handler.DefaultLogHandler     : [easy-log][测试post提交json数据]--end timeout:332 
```
最新swagger使用openapi3同样也是支持,@Operation(summary = "接口描述")开启自动记录日志,配置如下
```yaml
spring:
  easy-log:
    scan-open-api:
      enabled: true #支持openapi3注解记录日志,默认false
```

### 3.配置操作人
默认操作人为空,框架支持免开发设置操作人

从session中提取操作人示例
```yaml
spring:
  easy-log:
    operator: SESSION.account.username
```
"SESSION."代表从session中获取用户信息,account为保存在session中对象的名称。username为业务对象的字段

- SESSION.account.username等效于获取request.getSession().setAttribute(”account“, new Account("zhangsan"));设置的用户
- SESSION.loginName等效于获取request.getSession().setAttribute(”loginName“, "zhangsan");设置的用户

从请求头中提取操作人示例
```yaml
spring:
  easy-log:
    operator: HEADER.x-username
```
"HEADER."代表从请求头中获取用户信息。x-username代表请求头中的需要读取的变量.可以替换成业务中自己定义的,如x-login-name

> 操作人还支持从请求参数或实现接口自定义方式配置,具体参考使用教程

### 4.动态修改日志配置及在线日志
easy-log日志框架提供,动态修改日志配置能力,这部分是可选功能,如果开启需要补充easy-log-web依赖
```xml
        <!--日志页面管理模块(日志开关/同步异步/处理器切换)-->
        <dependency>
            <groupId>io.github.easycode8</groupId>
            <artifactId>easy-log-web</artifactId>
            <version>latest</version>
        </dependency>
```
访问: http://ip:port/easy-log-ui.html 账号密码:admin/admin123 账号密码可修改也可关闭
```yaml
spring:
  easy-log:
    web:
      enableBasicAuth: true #是否开启web访问认证, 默认true
      username: admin #默认账号
      password: admin123 #默认密码

```



### 5.开启数据快照功能
easy-log支持基于mybatis/mybatis-plus持久层框架,无任何手动编码自动记录修改和删除数据的快照数据,分析字段变化数据


- 添加maven依赖

```xml
        <!--mybatis-plus记录操作的数据变化模块-->
        <dependency>
            <groupId>io.github.easycode8</groupId>
            <artifactId>easy-log-data-mybatis-plus</artifactId>
            <version>latest</version>
        </dependency>
```
- 开启mybatis-plus mapper扫描

```yaml
spring:
  easy-log:
    scan-mybatis-plus:
      enabled: true #是否记录mybatis-plus/mybatis的mapper日志 默认:false
```
mybatis-plus目前支持自动记录修改接口有两种
- 接口定义
```java
public interface AccountMapper extends BaseMapper<Account> {

}

```
> 注意:使用mybatis-plus框架定义Dao/mapper必须继承mybatis-plus的BaseMapper<T>,泛型不要省略

- 使用示例
```java
        // 修改方式1: mybatis-plus提供updateById方法, 根据主键Id更新对象
        accountMapper.updateById(account);
        // 修改方式2: mybatis-plus提供update方法,修改数据和查询对象分离，命中多条也支持
        accountMapper.update(account, Wrappers.<Account>lambdaQuery()
            .eq(Account::getId, account.getId()));
```
mybatis目前支持自动记录快照支持按实体修改
- 接口定义
```java
public interface AccountMapper {

    int updateByPrimaryKey(Account account);
}
```
> 注意: 接口命名以Dap/Mapper结尾,参数为数据库对应的实体对象。
- 使用示例
```java
        //AccountMapper.xml中自定义方法updateByPrimaryKey, 入参为对象
        accountMapper.updateByPrimaryKey(account);
```
- 修改效果dataSnapshot:updated 为被修改的数据
```markdown
2023-08-11 11:25:18.177  INFO [sample-service,ff13310320ab4b2cb2cbb6351b3b4865] 39192 --- [nio-8000-exec-3] c.e.e.core.handler.DefaultLogHandler     : [easy-log][AccountMapper.updateById(entity)]--begin operator:[null] param:{"email":"aaa@qq.com","id":10001,"name":"dddd"}
2023-08-11 11:25:18.208  INFO [sample-service,ff13310320ab4b2cb2cbb6351b3b4865] 39192 --- [nio-8000-exec-3] c.e.e.m.i.DataSnapshotInterceptor        : [easy-log][AccountMapper.updateById(entity)] sql ==> UPDATE account SET name='dddd', email='aaa@qq.com' WHERE id=10001
2023-08-11 11:25:18.222  INFO [sample-service,ff13310320ab4b2cb2cbb6351b3b4865] 39192 --- [nio-8000-exec-3] c.e.e.m.i.DataSnapshotInterceptor        : [easy-log][AccountMapper.updateById(entity)] original data <== [{"email":"aaa@qq.com","id":10001,"name":"李四","username":"lisi"}]
2023-08-11 11:25:18.231  INFO [sample-service,ff13310320ab4b2cb2cbb6351b3b4865] 39192 --- [nio-8000-exec-3] c.e.e.core.handler.DefaultLogHandler     : [easy-log][AccountMapper.updateById(entity)]--end timeout:54 dataSnapshot:updated:[{"fieldName":"name","newValue":"dddd","oldValue":"李四"}]
```
- 删除效果dataSnapshot:deleted 为被删除数据
```markdown
2023-08-11 11:27:00.696  INFO [sample-service,2b2b9e544c8c42768f569368f772a16e] 39192 --- [nio-8000-exec-5] c.e.e.core.handler.DefaultLogHandler     : [easy-log][AccountServiceImpl.deleteAccount(id)]--begin operator:[null] param:10001
2023-08-11 11:27:00.696  INFO [sample-service,2b2b9e544c8c42768f569368f772a16e] 39192 --- [nio-8000-exec-5] c.e.e.core.handler.DefaultLogHandler     : [easy-log][AccountMapper.deleteById(id)]--begin operator:[null] param:10001
2023-08-11 11:27:00.696  INFO [sample-service,2b2b9e544c8c42768f569368f772a16e] 39192 --- [nio-8000-exec-5] c.e.e.m.i.DataSnapshotInterceptor        : [easy-log][AccountMapper.deleteById(id)] sql ==> DELETE FROM account WHERE id=10001
2023-08-11 11:27:00.700  INFO [sample-service,2b2b9e544c8c42768f569368f772a16e] 39192 --- [nio-8000-exec-5] c.e.e.m.i.DataSnapshotInterceptor        : [easy-log][AccountMapper.deleteById(id)] original data <== [{"password":"1234","sex":"","phone":"12345678912","username":"lisi","id":10001,"createTime":1644832562000,"updateTime":1645437371000,"email":"aaa@qq.com","name":"方式4"}]
2023-08-11 11:27:00.701  INFO [sample-service,2b2b9e544c8c42768f569368f772a16e] 39192 --- [nio-8000-exec-5] c.e.e.core.handler.DefaultLogHandler     : [easy-log][AccountMapper.deleteById(id)]--end timeout:5 dataSnapshot:deleted:[{"password":"1234","sex":"","phone":"12345678912","username":"lisi","id":10001,"createTime":1644832562000,"updateTime":1645437371000,"email":"aaa@qq.com","name":"方式4"}]
2023-08-11 11:27:00.701  INFO [sample-service,2b2b9e544c8c42768f569368f772a16e] 39192 --- [nio-8000-exec-5] c.e.e.core.handler.DefaultLogHandler     : [easy-log][AccountServiceImpl.deleteAccount(id)]--end timeout:5 
```
## 更多使用说明
详细入门教程/扩展用法/框架原理参考 [《easy-log使用教程》](https://easycode8.github.io/easy-log)


## 版本日志
### 1.0.12
1. 【优化】easy-log兼容java8
### 1.0.11
1. 【优化】easy-log-spring-boot-starter补充spring aop开启下才启动自动装配
2. 【功能】easy-log-core补充scanPackages功能,可设置项目业务的基础包名,减少无用的aop判断,提升启动效率
3. 【功能】easy-log-core补充监控功能提示日志aop增强性能,项目启动时间,端口等信息
4. 【重构】easy-log-core使用redis发布订阅模式优化集群场景下,日志记录点数据同步功能

### 1.0.10
1. 【功能】easy-log-web支持在线实时查看控制台日志
2. 【优化】easy-log条件装配优化

### 1.0.9
1. 【功能】支持OpenApi3的@Operation注解(summary字段)自动记录日志
2. 【优化】easy-log-core修复使用请求头获取操作人信息在非web场景执行报错问题

### 1.0.8
1. 【优化】easy-log-core默认打印内容优化
2. 【功能】easy-log-trace补充默认链路日志功能集成starter中
3. 【优化】easy-log-data-mybatis-plus自动获取数据快照失败不影响业务处理

### 1.0.7
1. 【优化】easy-log-core补充打印日志属性缓存类型
2. 【优化】easy-log-core优化处理无法序列化的请求入参
3. 【优化】easy-log-data-mybatis-plus优化mapper的方法名称--补充参数列表
4. 【优化】easy-log-core忽略controller继承父类的方法
5. 【优化】easy-log-data-mybatis-plus优化兼容实体存在非基础属性字段忽略处理
6. 【优化】easy-log-data-mybatis-plus优化mybatis @Param注解标识的更新接口
7. 【优化】easy-log-data-mybatis-plus优化delete from提取查询语句兼容忽略from的场景
8. 【功能】easy-log-trace日志链路模块--整合sleuth/zipkin模块--初稿

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