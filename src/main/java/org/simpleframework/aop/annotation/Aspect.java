package org.simpleframework.aop.annotation;

import java.lang.annotation.*;

/**
 * 切面
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Aspect {
    /**
     * 定义切面类型的时候，暂时只能通过注解去定义，比如说我们定义为controller，就为所有的controller添加切面
     * @return
     */
    Class<? extends Annotation> value();
}
