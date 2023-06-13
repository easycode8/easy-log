# 框架设计
## 模块说明

|模块|功能|备注|
|---|---|---|
|easy-log-core|日志抽象封装,及约定接口|核心模块|
|easy-log-spring-boot-starter|将core模块封装成starter|服务模块,依赖core|
|easy-log-web|日志增强在线动态修改日志关键配置|可选模块,组合starter使用|
|easy-log-data-mybatis-plus|基于mybatis-plus持久层的数据快照|可选模块,组合starter使用,依赖mybatis-plus|
|easy-log-trace|可以整合sleuth/zipkin日志链路starter自动引入,可使用默认也可以依赖对接zipkin实现链路日志的增强|


