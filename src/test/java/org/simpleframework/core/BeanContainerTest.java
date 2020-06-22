package org.simpleframework.core;

import com.liuyj.controller.frontend.MainPageController;
import com.liuyj.service.solo.HeadLineService;
import org.junit.jupiter.api.*;
import org.simpleframework.core.annotation.Controller;

import java.util.Set;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class BeanContainerTest {

    private static BeanContainer beanContainer;

    @BeforeAll
    static void init() {
        beanContainer = BeanContainer.getInstance();
    }

    @Test
    @Order(1)
    @DisplayName("测试加载Bean：loadBeanTest")
    public void loadBeanTest() {
        Assertions.assertEquals(false, beanContainer.isLoaded());
        beanContainer.loadBeans("com.liuyj");
        Assertions.assertEquals(6, beanContainer.size());
        Assertions.assertEquals(true, beanContainer.isLoaded());

    }

    @Test
    @Order(2)
    @DisplayName("测试获取Bean")
    public void getBeanTest(){
        Assertions.assertEquals(true, beanContainer.isLoaded());
        MainPageController mainPageController = (MainPageController) beanContainer.getBean(MainPageController.class);
        Assertions.assertEquals(true, mainPageController instanceof MainPageController);
    }

    @Test
    @Order(2)
    @DisplayName("测试根据注解获取对应的Class")
    public void getClassesByAnnotationTest(){
        Assertions.assertEquals(true, beanContainer.isLoaded());
        Set<Class<?>> classSet = beanContainer.getClassesByAnnotation(Controller.class);
        Assertions.assertEquals(3, classSet.size());
    }

    @Test
    @Order(3)
    @DisplayName("测试根据传入的接口或父类，获取对应实现类和子类的class")
    public void getClassesBySuperTest(){
        Assertions.assertEquals(true, beanContainer.isLoaded());
        Set<Class<?>> classSet = beanContainer.getClassesBySuper(HeadLineService.class);
        Assertions.assertEquals(1, classSet.size());
    }
}
