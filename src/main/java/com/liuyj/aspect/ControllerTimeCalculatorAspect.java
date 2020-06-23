package com.liuyj.aspect;

import lombok.extern.slf4j.Slf4j;
import org.simpleframework.aop.annotation.Aspect;
import org.simpleframework.aop.annotation.Order;
import org.simpleframework.aop.aspect.DefaultAspect;
import org.simpleframework.core.annotation.Controller;

import java.lang.reflect.Method;

/**
 * 一个对Controller方法执行记时的Aspect
 */
@Slf4j
@Order(0)
@Aspect(pointcut = "execution(* com.liuyj.controller.frontend..*.*(..))")
public class ControllerTimeCalculatorAspect extends DefaultAspect {

    private long timeStampCache;

    @Override
    public void before(Class<?> targetClass, Method method, Object[] args) throws Throwable {
        log.info("开始计时，执行的类是[{}]，执行的方法是[{}]，参数是[{}]",
                targetClass.getName(), method.getName(), args);
        timeStampCache = System.currentTimeMillis();
    }

    @Override
    public Object afterReturning(Class<?> targetClass, Method method, Object[] args, Object returnValue) throws Throwable {
        long endTime = System.currentTimeMillis();
        long costTime = endTime - timeStampCache;
        log.info("执行的类是[{}]，执行的方法是[{}]，参数是[{}]，返回值是[{}]，总耗时：[{}]",
                targetClass.getName(), method.getName(), args, returnValue, costTime);
        return returnValue;
    }
}

