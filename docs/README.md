[comment]: <> (![]&#40;https://visitor-badge.laobi.icu/badge?page_id=easy-log&#41;)
# 简介

[comment]: <> (![GitHub Repo stars]&#40;https://img.shields.io/github/stars/easycode8/easy-log?style=flat&#41;)
![Maven Central](https://img.shields.io/maven-central/v/io.github.easycode8/easy-log-core)
> easy-log是基于Spring AOP实现的执行日志处理工具,可以快速帮助用户实现操作日志/审计日志的功能

|源码|你的star是更新的最大动力|
|---|---|
|github|https://github.com/easycode8/easy-log.git|
|gitee|https://gitee.com/easycode8/easy-log.git|


- **低入侵**:
  1. 默认引入依赖,无需改动任何代码即可实现日志记录。
  2. 不强依赖某种日志注解,可以自己定义。或者复用现有的设计。
- **扩展性**: 
  1. 支持使用@EasyLog注解/自定义日志注解/无日志注解注标记记录操作日志-【注解不是必须的】
  2. 支持配置session/header/请求参数/扩展接口实现获取操作人【前三种无需编码】
  3. 支持SpEL表达式定义日志**动态内容模板**
  4. 支持**自定义标签Tags**定义业务属性
  5. 支持使用自定义对象**扩展日志字段** 【日志对象不再写死】
  6. 支持不同日志处理器handler同时使用【不同日志不同处理】
  7. 支持方法级日志处理策略handleMode【同步/异步/全局默认)】
  8. 提供了Web管理界面，可以在线动态控制日志功能的启停、同步异步切换和日志处理器切换等功能
  9. 提供日志链路功能,也可以整合sleuth/zipkin做链路日志的增强
  

- **友好性**:
  1. 完善的文档信息,包含各种扩展点演示代码示例
  2. 支持swagger/knife4j定义接口自动记录执行日志,一键配置【真的不需要那么多注解】
  3. 支持controller/service接口方法一键开启执行日志 【接口方法/入参/请求耗时/异常报错一目了然】  
  4. 支持mybatis/mybatis-plus mapper操作日志记录,支持记录数据快照(修改操作及删除操作)  
  5. 支持StopWatch测试日志增强处理性能开销 
    