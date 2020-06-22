package org.simpleframework.aop;

import com.liuyj.controller.frontend.MainPageController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.BeanContainer;
import org.simpleframework.inject.DependencyInjector;

public class AspectWeaverTest {

    @Test
    @DisplayName("织入通用测试逻辑：doAop")
    public void doAopTest(){
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.liuyj");

        new AspectWeaver().doAop();
        new DependencyInjector().doIoc();

        MainPageController mainPageController = (MainPageController) beanContainer.getBean(MainPageController.class);
        mainPageController.sayHi();
    }
}
