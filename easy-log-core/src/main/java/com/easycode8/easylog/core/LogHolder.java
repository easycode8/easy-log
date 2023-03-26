package com.easycode8.easylog.core;

import org.springframework.core.NamedThreadLocal;

import java.util.ArrayDeque;
import java.util.Deque;

public class LogHolder {
    /**
     * 为什么不能用String 而要使用队列(这里作为栈实现)来存储
     * 因为每次一个方法做多aop增强时候。都会在进入方法前为当前线程绑定当前日志信息。结束时候清除信息
     * 如果是使用同一个变量,那么aop增强方法内部还又带有AOP增加的方法，那么进入时候就会覆盖外出方法,同时内部方法aop结束时候,日志信息被清除
     * 外部线程拿到的日志信息则会出现空指针
     */
    private static final ThreadLocal<Deque<LogInfo>> LOG_DEQUE = new NamedThreadLocal<Deque<LogInfo>>("LOG_DEQUE") {
        @Override
        protected Deque<LogInfo> initialValue() {
            return new ArrayDeque<>();
        }
    };


    /**
     * 入栈
     */
    public static void push(LogInfo info) {
        LOG_DEQUE.get().push(info);
    }

    /**
     * 获取栈顶元素
     * @return
     */
    public static LogInfo peek() {
        return LOG_DEQUE.get().peek();

    }



    /**
     * 移除栈顶数据源,如果是最后元素,则清空线程数据
     */
    public static void poll() {
        Deque<LogInfo> deque = LOG_DEQUE.get();
        deque.poll();
        if (deque.isEmpty()) {
            LOG_DEQUE.remove();
        }
    }

}
