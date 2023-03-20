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