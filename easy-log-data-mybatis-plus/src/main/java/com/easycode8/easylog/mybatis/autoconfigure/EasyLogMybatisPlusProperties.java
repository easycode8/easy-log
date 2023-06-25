package com.easycode8.easylog.mybatis.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.easy-log")
public class EasyLogMybatisPlusProperties {

    private ScanMybatisPlus scanMybatisPlus;

    public static class ScanMybatisPlus {
        /**是否开启扫描mybatis-plus mapper接口方法(必须继承BaseMapper.class)*/
        private Boolean enabled = false;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }

    public ScanMybatisPlus getScanMybatisPlus() {
        return scanMybatisPlus;
    }

    public void setScanMybatisPlus(ScanMybatisPlus scanMybatisPlus) {
        this.scanMybatisPlus = scanMybatisPlus;
    }
}