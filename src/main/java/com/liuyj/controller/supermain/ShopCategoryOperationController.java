package com.liuyj.controller.supermain;

import com.liuyj.service.solo.ShopCategoryService;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.inject.annotation.Autowired;

@Controller
public class ShopCategoryOperationController {

    @Autowired
    private ShopCategoryService shopCategoryService;

}
