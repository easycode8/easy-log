package com.easycode8.easylog.core.monitor;


import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.aop.interceptor.TimingLogAttributeSourcePointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.availability.AvailabilityChangeEvent;
import org.springframework.boot.availability.ReadinessState;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.Optional;

/**
 * 打印应用相关上下文信息
 */
public class EasyLogApplicationInfoPrinter implements ApplicationListener<AvailabilityChangeEvent<ReadinessState>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyLogApplicationInfoPrinter.class);

    @Value("${easy-log.monitor.printServerInfo:true}")
    private boolean printServerInfo;


    private EasyLogProperties easyLogProperties;

    @Override
    public void onApplicationEvent(AvailabilityChangeEvent<ReadinessState> availabilityChangeEvent) {
        // ReadinessState.ACCEPTING_TRAFFIC 表示可以接受流量
        if (availabilityChangeEvent.getState() == ReadinessState.ACCEPTING_TRAFFIC && this.printServerInfo) {
            try {
                ConfigurableApplicationContext context = (ConfigurableApplicationContext) availabilityChangeEvent.getSource();

                ConfigurableEnvironment environment = context.getEnvironment();
                String serverPort = getApplicationServerPort(environment);
                LOGGER.info("==============================");
                LOGGER.info("[easy-log] aop扫描加载耗时:{}毫秒", TimingLogAttributeSourcePointcut.getTotalAopMatchesTime());
                // 获取Java虚拟机内存管理的MXBean
                MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
                LOGGER.info("[easy-log] JVM堆内存使用情况: 初始值={}MB, 最大值={}MB, 当前值={}MB",
                        heapUsage.getInit() / 1024 / 1024,
                        heapUsage.getMax() / 1024 / 1024,
                        heapUsage.getUsed() / 1024 / 1024);

                LOGGER.info("[easy-log] 项目启动端口: {},项目启动耗时: {} 秒", serverPort, ManagementFactory.getRuntimeMXBean().getUptime() / 1000f);
                LOGGER.info("==============================");
            } catch (Exception e) {
                // ignore
            }
        }
    }

    public static String getApplicationServerPort(Environment environment) {
        return Optional.ofNullable(environment.getProperty("local.server.port"))
                .orElse(environment.getProperty("server.port"));
    }

}