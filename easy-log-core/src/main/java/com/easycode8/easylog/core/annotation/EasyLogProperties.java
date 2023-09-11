package com.easycode8.easylog.core.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.easy-log")
public class EasyLogProperties {

    /** 是否启用easy-log 默认true*/
    private Boolean enabled = true;

    /**
     * 获取操作人信息的表达式 支持从session或请求头获取 默认为空示例:
     * <li>spring.easy-log.operator=session.account.username:通过session获取属性为account中对象的username字段作为操作人</li>
     * <li>spring.easy-log.operator=header.x-username:代表从请求头中获取x-username作为操作人信息 </li>
     */
    private String operator = "";

    /**是否开启默认异步记录日志 默认false*/
    private Boolean async = false;

    // spring顺序默认是Ordered.LOWEST_PRECEDENCE(Integer.MAX_VALUE)优先级最低，如果两个aspect顺序一样，则使用bean注册的顺序
    /**切面的顺序 值越小越先执行Integer.MIN_VALUE 优先级最高, 设置为0 比一般spring 默认最低优先级高*/
    private Integer aspectOrder = 0;

    private Task task = new Task();

    private ScanOpenApi scanOpenApi;

    private ScanSwagger scanSwagger;

    private ScanService scanService;

    private ScanController scanController;

    private Cache cache = new Cache();


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    public ScanService getScanService() {
        return scanService;
    }

    public void setScanService(ScanService scanService) {
        this.scanService = scanService;
    }

    public ScanController getScanController() {
        return scanController;
    }

    public void setScanController(ScanController scanController) {
        this.scanController = scanController;
    }

    public ScanOpenApi getScanOpenApi() {
        return scanOpenApi;
    }

    public void setScanOpenApi(ScanOpenApi scanOpenApi) {
        this.scanOpenApi = scanOpenApi;
    }

    public ScanSwagger getScanSwagger() {
        return scanSwagger;
    }

    public void setScanSwagger(ScanSwagger scanSwagger) {
        this.scanSwagger = scanSwagger;
    }

    public Boolean getAsync() {
        return async;
    }

    public void setAsync(Boolean async) {
        this.async = async;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Integer getAspectOrder() {
        return aspectOrder;
    }

    public void setAspectOrder(Integer aspectOrder) {
        this.aspectOrder = aspectOrder;
    }

    public Cache getCache() {
        return cache;
    }

    public void setCache(Cache cache) {
        this.cache = cache;
    }



    public static class Task {


        /**核心线程数定义了最小可以同时运行的线程数量*/
        private Integer corePoolSize = 4;
        /**当队列中存放的任务达到队列容量的时候，当前可以同时运行的线程数量变为最大线程数*/
        private Integer maxPoolSize = 20;
        /**任务队列，被提交但尚未被执行的任务*/
        private Integer queueCapacity = 50;
        /**当线程池中的线程数量大于 corePoolSize 的时候，如果这时没有新的任务提交，核心线程外的线程不会立即销毁，而是会等待，直到等待的时间超过了 keepAliveTime才会被回收销毁*/
        private Integer keepAliveSeconds = 1800;
        /**线程名称前缀*/
        private String threadNamePrefix = "easy-log-";

        public Integer getCorePoolSize() {
            return corePoolSize;
        }

        public void setCorePoolSize(Integer corePoolSize) {
            this.corePoolSize = corePoolSize;
        }

        public Integer getMaxPoolSize() {
            return maxPoolSize;
        }

        public void setMaxPoolSize(Integer maxPoolSize) {
            this.maxPoolSize = maxPoolSize;
        }

        public Integer getQueueCapacity() {
            return queueCapacity;
        }

        public void setQueueCapacity(Integer queueCapacity) {
            this.queueCapacity = queueCapacity;
        }

        public Integer getKeepAliveSeconds() {
            return keepAliveSeconds;
        }

        public void setKeepAliveSeconds(Integer keepAliveSeconds) {
            this.keepAliveSeconds = keepAliveSeconds;
        }

        public String getThreadNamePrefix() {
            return threadNamePrefix;
        }

        public void setThreadNamePrefix(String threadNamePrefix) {
            this.threadNamePrefix = threadNamePrefix;
        }
    }

    public static class ScanOpenApi {
        /**是否开启openapi3 的@Operation注解作为日志标识*/
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class ScanSwagger {
        /**是否开启swagger的@ApiOperation注解作为日志标识*/
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class ScanService {
        /**是否开启扫描@Service标记的公开方法*/
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class ScanController {
        /**是否开启扫描Controller的接口方法*/
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

    public static class Cache {
        /**easy-log日志属性缓存前缀 默认: easy-log::*/
        private String keyPrefix = "easy-log::";

        public String getKeyPrefix() {
            return keyPrefix;
        }

        public void setKeyPrefix(String keyPrefix) {
            this.keyPrefix = keyPrefix;
        }
    }

}