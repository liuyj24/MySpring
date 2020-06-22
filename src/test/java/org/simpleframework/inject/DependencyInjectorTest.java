package org.simpleframework.inject;


import com.liuyj.controller.frontend.MainPageController;
import com.liuyj.service.combine.impl.HeadLineShopCategoryCombineServiceImpl;
import com.liuyj.service.combine.impl.HeadLineShopCategoryCombineServiceImpl2;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.simpleframework.core.BeanContainer;

public class DependencyInjectorTest {

    @Test
    @DisplayName("测试doIoc")
    public void doIocTest(){
        BeanContainer beanContainer = BeanContainer.getInstance();
        beanContainer.loadBeans("com.liuyj");
        Assertions.assertEquals(true, beanContainer.isLoaded());

        MainPageController mainPageController = (MainPageController) beanContainer.getBean(MainPageController.class);
        Assertions.assertEquals(true, mainPageController instanceof MainPageController);

        Assertions.assertEquals(null, mainPageController.getHeadLineShopCategoryCombineService());

        new DependencyInjector().doIoc();
        Assertions.assertNotEquals(null, mainPageController.getHeadLineShopCategoryCombineService());

        Assertions.assertEquals(true, mainPageController.getHeadLineShopCategoryCombineService() instanceof HeadLineShopCategoryCombineServiceImpl);
        Assertions.assertEquals(false, mainPageController.getHeadLineShopCategoryCombineService() instanceof HeadLineShopCategoryCombineServiceImpl2);

    }


}
