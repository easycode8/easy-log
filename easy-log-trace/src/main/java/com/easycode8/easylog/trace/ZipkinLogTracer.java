package com.easycode8.easylog.trace;



import brave.Span;
import brave.Tracer;
import com.easycode8.easylog.core.LogInfo;
import com.easycode8.easylog.core.trace.LogTracer;
import org.springframework.core.NamedThreadLocal;
import org.springframework.util.StringUtils;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * 基于zipkin的日志追踪器
 */
public class ZipkinLogTracer implements LogTracer {


    private static final ThreadLocal<Deque<Span>> SPAN_DEQUE = new NamedThreadLocal<Deque<Span>>("SPAN_DEQUE") {
        @Override
        protected Deque<Span> initialValue() {
            return new ArrayDeque<>();
        }
    };


    private final Tracer tracer;

    public ZipkinLogTracer(Tracer tracer) {
        this.tracer = tracer;
    }

    @Override
    public void init(LogInfo info) {

        Span span = this.tracer.nextSpan().kind(Span.Kind.SERVER).start();
        info.setTraceId(span.context().traceIdString());
        push(span);
    }

    @Override
    public void start(LogInfo info) {
        Span span = peek();
        String spanName = info.getMethod();
        if (!info.getTitle().equals(info.getMethod())) {
            spanName = spanName + "//" + info.getTitle();
        }
        span.name(spanName);
        span.tag("param", info.getParams());
        span.tag("title", info.getTitle());


        if (StringUtils.hasText(info.getRequestUri())) {
            span.tag("uri", info.getRequestUri());
        }

        if (StringUtils.hasText(info.getOperator())) {
            span.tag("operator", info.getOperator());
        }

        if (StringUtils.hasText(info.getDescription())) {
            span.tag("description", info.getDescription());
        }

        if (StringUtils.hasText(info.getException())) {
            span.tag("exception", info.getException());
        }

        if (StringUtils.hasText(info.getTags())) {
            span.tag("tags", info.getTags());
        }

        if (StringUtils.hasText(info.getIp())) {
            span.tag("ip", info.getIp());
        }

        if (StringUtils.hasText(info.getException())) {
            span.tag("exception", info.getException());
        }


    }

    @Override
    public void finish(LogInfo info) {
        Span span = peek();
        span.tag("timeout", info.getTimeout().toString());
        if (StringUtils.hasText(info.getDataSnapshot())) {
            span.tag("dataSnapshot", info.getDataSnapshot());
        }
        span.finish();
        poll();

    }

    /**
     * 入栈
     */
    public static void push(Span span) {
        SPAN_DEQUE.get().push(span);
    }

    /**
     * 获取栈顶元素
     *
     * @return
     */
    public static Span peek() {
        return SPAN_DEQUE.get().peek();

    }


    /**
     * 移除栈顶数据源,如果是最后元素,则清空线程数据
     */
    public static void poll() {
        Deque<Span> deque = SPAN_DEQUE.get();
        deque.poll();
        if (deque.isEmpty()) {
            SPAN_DEQUE.remove();
        }
    }
}
