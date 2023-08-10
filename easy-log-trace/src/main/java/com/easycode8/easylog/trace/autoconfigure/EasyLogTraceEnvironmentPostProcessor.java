package com.easycode8.easylog.trace.autoconfigure;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * 修改日志输出格式增加traceId记录, 如果sleuth存在且开启则优先使用sleuth作为链路追踪
 *
 * @see org.springframework.cloud.sleuth.autoconfig.TraceEnvironmentPostProcessor
 */
public class EasyLogTraceEnvironmentPostProcessor implements EnvironmentPostProcessor {

    private static final String PROPERTY_SOURCE_NAME = "easylog- defaultProperties";

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment,
                                       SpringApplication application) {

        Map<String, Object> map = new HashMap<String, Object>();
        // This doesn't work with all logging systems but it's a useful default so you see
        // traces in logs without having to configure it.
        if (Boolean.parseBoolean(environment.getProperty("spring.easy-log.trace.enabled", "true")) && !this.existSleuthTrace(environment)) {
            map.put("logging.pattern.level", "%5p [${spring.application.name:},%X{X-Trace-Id:-}]");
        }
        addOrReplace(environment.getPropertySources(), map);
    }

    private void addOrReplace(MutablePropertySources propertySources,
                              Map<String, Object> map) {
        MapPropertySource target = null;
        if (propertySources.contains(PROPERTY_SOURCE_NAME)) {
            PropertySource<?> source = propertySources.get(PROPERTY_SOURCE_NAME);
            if (source instanceof MapPropertySource) {
                target = (MapPropertySource) source;
                for (String key : map.keySet()) {
                    if (!target.containsProperty(key)) {
                        target.getSource().put(key, map.get(key));
                    }
                }
            }
        }
        if (target == null) {
            target = new MapPropertySource(PROPERTY_SOURCE_NAME, map);
        }
        if (!propertySources.contains(PROPERTY_SOURCE_NAME)) {
            propertySources.addLast(target);
        }
    }

    /**
     * sleuth的链路追踪是否生效
     *
     * @return
     */
    private boolean existSleuthTrace(ConfigurableEnvironment environment) {
        boolean flag = false;
        // 判断另一个类是否存在
        try {
            Class.forName("org.springframework.cloud.sleuth.autoconfig.TraceEnvironmentPostProcessor");
            flag = true;
        } catch (ClassNotFoundException e) {
            // 另一个类不存在，执行后续处理逻辑
        }
        // 如果TraceEnvironmentPostProcessor 存在且spring.sleuth.enabled 开着, 则使用 sleuth的日志格式
        return flag && Boolean.parseBoolean(environment.getProperty("spring.sleuth.enabled", "true"));
    }


}
