package org.simpleframework.aop.annotation;

import java.lang.annotation.*;

/**
 * 切面
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {

    String pointcut();

}
