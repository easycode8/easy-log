package com.easycode8.easylog.trace.filter;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * 记录TRACE_ID
 */
public class EasyLogTraceFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(EasyLogTraceFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest)servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        try {
            mdc(request, response);
        } catch (Exception e) {
            LOGGER.warn("record mdc logger error:{}", e.getMessage());
        }

        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.clear();
        }

    }

    private void mdc(HttpServletRequest request, HttpServletResponse response) {
        String traceId = StringUtils.isEmpty(request.getHeader(MDCConstants.TRACE_ID))? UUID.randomUUID().toString().replace("-","") :request.getHeader(MDCConstants.TRACE_ID);
        MDC.put(MDCConstants.TRACE_ID, traceId);
        // 设置响应traceId
        if (!StringUtils.isEmpty(traceId)) {
            response.setHeader(MDCConstants.TRACE_ID, traceId);
        }

    }

}