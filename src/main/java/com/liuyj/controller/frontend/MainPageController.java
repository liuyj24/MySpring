package com.liuyj.controller.frontend;

import com.liuyj.service.combine.HeadLineShopCategoryCombineService;
import lombok.Getter;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.inject.annotation.Autowired;

@Getter
@Controller
public class MainPageController {

    @Autowired(value = "HeadLineShopCategoryCombineServiceImpl")
    private HeadLineShopCategoryCombineService headLineShopCategoryCombineService;

    public void sayHi(){
        System.out.println("Hi~");
    }

}
