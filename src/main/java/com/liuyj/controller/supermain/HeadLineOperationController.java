package com.liuyj.controller.supermain;

import com.liuyj.service.solo.HeadLineService;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.inject.annotation.Autowired;

@Controller
public class HeadLineOperationController {

    @Autowired
    private HeadLineService headLineService;


}
