package com.easycode8.easylog.core.aop.interceptor;

import com.easycode8.easylog.core.adapter.LogAttributeMappingAdapter;
import com.easycode8.easylog.core.annotation.EasyLog;
import com.easycode8.easylog.core.annotation.EasyLogProperties;
import com.easycode8.easylog.core.annotation.Tag;
import com.easycode8.easylog.core.util.LogUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnnotationLogAttributeSource implements LogAttributeSource {

    private final EasyLogProperties easyLogProperties;

    private final List<LogAttributeMappingAdapter> mappingAdapters;

    public AnnotationLogAttributeSource(EasyLogProperties easyLogProperties, List<LogAttributeMappingAdapter> mappingAdapters) {
        this.easyLogProperties = easyLogProperties;
        this.mappingAdapters = mappingAdapters;
    }


    @Override
    public LogAttribute getLogAttribute(Method method, Class<?> targetClass) {
        EasyLog easyLog = method.getAnnotation(EasyLog.class);
        // EasyLog注解优先级最高
        if (easyLog != null) {
            String title = StringUtils.isEmpty(easyLog.title()) ? easyLog.value() : easyLog.title();
            // 如果都没有定义标题使用默认标题
            if (StringUtils.isEmpty(title)) {
                title = LogUtils.createDefaultTitle(method, targetClass);
            }
            Tag[] tags = easyLog.tags();
            Map<String, String> tagMap = null;
            if (tags != null) {
                tagMap = Arrays.stream(tags)
                        // 提出key为空的数据
                        .filter(item -> StringUtils.hasText(item.key()))
                        .map(p -> Map.entry(p.key(), p.value()))
                        // 将 Map.Entry 对象转为 Map
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

            }

            // 如果注解指定是否同步优先级最高,否则读取项目默认配置值
            boolean async = false;
            switch (easyLog.handleMode()) {
                case GLOBAL: async = easyLogProperties.getAsync(); break;
                case ASYNC: async = true; break;
                case SYNC: async = false; break;
            }

            return DefaultLogAttribute.builder()
                    .title(title)
                    .handler(easyLog.handler())
                    .template(easyLog.template())
                    .async(async)
                    .tags(tagMap)
                    .build();
        }
        // 从映射适配器中获取自定义的日志属性
        if (CollectionUtils.isEmpty(mappingAdapters)) {
            return null;
        }

        for (LogAttributeMappingAdapter mappingAdapter : mappingAdapters) {
            LogAttribute logAttribute = mappingAdapter.getLogAttribute(method, targetClass);
            if (logAttribute != null) {
                return logAttribute;
            }
        }
        return null;
    }

}
