package com.easycode8.easylog.web.controller;


import com.alibaba.fastjson.JSON;
import com.easycode8.easylog.core.aop.interceptor.AbstractCacheLogAttributeSource;
import com.easycode8.easylog.core.aop.interceptor.DefaultLogAttribute;
import com.easycode8.easylog.core.aop.interceptor.LogAttribute;
import com.easycode8.easylog.web.model.param.LogAttributeParam;
import com.easycode8.easylog.web.model.vo.LogAttributeVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping("/easy-log")
public class EasyLogController {

    @Autowired
    private AbstractCacheLogAttributeSource logAttributeSource;

    @GetMapping()
    public String index(HttpServletRequest request, HttpServletResponse response, ModelMap model) {

        String apiPrefix = "/";
        model.addAttribute("apiUrl", apiPrefix);
        Map<String, Object> front = new HashMap<>();

        front.put("iview_css", apiPrefix + "webjars/easy-log/iview/style/iview.css");
        front.put("iview_js", apiPrefix + "webjars/easy-log/iview/iview.min.js");
        front.put("vue_js", apiPrefix + "webjars/easy-log/vue/vue.js");
        front.put("axios_js", apiPrefix + "webjars/easy-log/axios/axios.min.js");
        model.addAttribute("front", front);
        return "easy-log";
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list() {
        Map<String, Object> result = new HashMap<>();
        if (CollectionUtils.isEmpty(logAttributeSource.getCacheMap())) {
            return ResponseEntity.ok(result);
        }
        List<DefaultLogAttribute> data = new ArrayList<>();
        for (Map.Entry<String, LogAttribute> item : logAttributeSource.getCacheMap().entrySet()) {
            LogAttributeVO attribute = JSON.parseObject(JSON.toJSONString(item.getValue()), LogAttributeVO.class);
            attribute.setMethod(item.getKey());
            data.add(attribute);
        }
        result.put("data", data);
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
        }

//        result.put("data", data);
        return ResponseEntity.ok(result);
    }


}
