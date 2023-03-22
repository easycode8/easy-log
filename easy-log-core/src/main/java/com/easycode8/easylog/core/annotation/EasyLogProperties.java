package com.easycode8.easylog.core.annotation;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.easy-log")
public class EasyLogProperties {

    /**
     * 获取操作人信息的表达式 支持从session或请求头获取 默认为空示例:
     * <li>spring.easy-log.operator=session.account.username:通过session获取属性为account中对象的username字段作为操作人</li>
     * <li>spring.easy-log.operator=header.x-username:代表从请求头中获取x-username作为操作人信息 </li>
     */
    private String operator = "";

    /**是否开启默认异步记录日志 默认false*/
    private Boolean async = false;

    private Task task = new Task();

    private ScanSwagger scanSwagger;

    private ScanService scanService;

    private ScanController scanController;

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

    public static class Task {


        /**核心线程数定义了最小可以同时运行的线程数量*/
        private Integer corePoolSize;
        /**当队列中存放的任务达到队列容量的时候，当前可以同时运行的线程数量变为最大线程数*/
        private Integer maxPoolSize;
        /**任务队列，被提交但尚未被执行的任务*/
        private Integer queueCapacity;
        /**当线程池中的线程数量大于 corePoolSize 的时候，如果这时没有新的任务提交，核心线程外的线程不会立即销毁，而是会等待，直到等待的时间超过了 keepAliveTime才会被回收销毁*/
        private Integer keepAliveSeconds;
        /**线程名称前缀*/
        private String threadNamePrefix = "easy-log-";

        public Task() {
            // 默认线程池配置,未指定属性情况
            this.corePoolSize = 4;
            this.maxPoolSize = 20;
            this.queueCapacity = 50;
            this.keepAliveSeconds = 1800;
        }



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

}