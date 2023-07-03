package com.easycode8.easylog.web.controller;


import com.alibaba.fastjson.JSON;
import com.easycode8.easylog.core.handler.LogDataHandler;
import com.easycode8.easylog.core.aop.interceptor.AbstractCacheLogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import com.easycode8.easylog.web.model.PageInfo;
import com.easycode8.easylog.web.model.param.LogAttributeParam;
import com.easycode8.easylog.web.model.vo.LogAttributeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/easy-log")
public class EasyLogController {

    @Autowired
    private AbstractCacheLogAttributeSource logAttributeSource;


    @Autowired
    private ApplicationContext applicationContext;


    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(LogAttributeParam param, PageInfo<Map<String, Object>> pageInfo) {
        Map<String, Object> result = new HashMap<>();
        if (CollectionUtils.isEmpty(logAttributeSource.getCacheMap())) {
            return ResponseEntity.ok(result);
        }
        List<LogAttributeVO> data = new ArrayList<>();
        for (Map.Entry<String, LogAttribute> item : logAttributeSource.getCacheMap().entrySet()) {
            LogAttributeVO attribute = JSON.parseObject(JSON.toJSONString(item.getValue()), LogAttributeVO.class);
            attribute.setMethod(item.getKey());
            attribute.setTemplate(StringUtils.isEmpty(attribute.template()) ? "": attribute.template());
            attribute.setHandler(StringUtils.isEmpty(attribute.getHandler()) ? "easyLogDataHandler": attribute.getHandler());
            data.add(attribute);
        }

        List<LogAttributeVO> filterList = data.stream()
                .filter(item -> item.getTitle().contains(param.getTitle()))
                .filter(item -> item.getTemplate().contains(param.getTemplate()))
                .filter(item -> param.getActive() == null || (param.getActive() != null && item.active().equals(param.getActive())))

                .sorted(Comparator.comparing(LogAttributeVO::getMethod)).collect(Collectors.toList());
        pageInfo.doPage(filterList,(item) -> item);
        result.put("data", pageInfo);
        return ResponseEntity.ok(result);
    }

    @PostMapping("update")
    public ResponseEntity<Map<String, Object>> update(@RequestBody LogAttributeParam param) {
        Map<String, Object> result = new HashMap<>();
        if (logAttributeSource.getCacheMap().containsKey(param.getMethod())) {
            DefaultLogAttribute logAttribute = (DefaultLogAttribute) logAttributeSource.getCacheMap().get(param.getMethod());
            logAttribute.setActive(param.getActive());
            logAttribute.setAsync(param.getAsync());
            logAttribute.setHandler(param.getHandler());
            logAttributeSource.updateCache(param.getMethod(), logAttribute);
        }
        result.put("message", "动态修改配置成功！");
        return ResponseEntity.ok(result);
    }
    @GetMapping("param")
    public ResponseEntity<Map<String, Object>> queryParam() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> dict = new HashMap<>();
        String[] beanNames = applicationContext.getBeanNamesForType(LogDataHandler.class);
        dict.put("handler", Arrays.asList(beanNames));
        result.put("data", dict);
        return ResponseEntity.ok(result);
    }


}
