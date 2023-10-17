package com.easycode8.easylog.core.aop.interceptor;

import java.lang.reflect.Method;
import java.util.concurrent.atomic.AtomicLong;

public abstract class TimingLogAttributeSourcePointcut extends LogAttributeSourcePointcut {
    private static AtomicLong totalMatchesTime = new AtomicLong(0);

    @Override
    public boolean matches(Method method, Class<?> targetClass) {
        long startTime = System.currentTimeMillis();

        boolean result = super.matches(method, targetClass);

        long endTime = System.currentTimeMillis();
        long elapsedTime = endTime - startTime;
        totalMatchesTime.addAndGet(elapsedTime);

        return result;
    }

    // 在项目启动后调用此方法打印总耗时
    public static long getTotalAopMatchesTime() {
        //System.out.println("Total matches() execution time: " + totalMatchesTime.get() + " ms");
        return totalMatchesTime.get();
    }
}
