package com.liuyj.controller.frontend;

import com.liuyj.entity.bo.HeadLine;
import com.liuyj.entity.dto.Result;
import com.liuyj.service.combine.HeadLineShopCategoryCombineService;
import lombok.Getter;
import org.simpleframework.core.annotation.Controller;
import org.simpleframework.inject.annotation.Autowired;
import org.simpleframework.mvc.annotation.RequestMapping;
import org.simpleframework.mvc.annotation.RequestParam;
import org.simpleframework.mvc.annotation.ResponseBody;
import org.simpleframework.mvc.type.ModelAndView;
import org.simpleframework.mvc.type.RequestMethod;

import java.util.List;

@Getter
@Controller
@RequestMapping(value = "/mainPage")
public class MainPageController {

    @Autowired(value = "HeadLineShopCategoryCombineServiceImpl")
    private HeadLineShopCategoryCombineService headLineShopCategoryCombineService;

    @RequestMapping(value = "/sayHi", method = RequestMethod.GET)
    public void sayHi() {
        System.out.println("Hi~");
    }

    @RequestMapping(value = "/testError", method = RequestMethod.GET)
    public void testError() {
        throw new RuntimeException("服务器内部错误");
    }

    @ResponseBody
    @RequestMapping(value = "/queryAll", method = RequestMethod.GET)
    public Result<List<HeadLine>> queryAll(){
        return this.headLineShopCategoryCombineService.queryAll();
    }

    @RequestMapping(value = "testView", method = RequestMethod.POST)
    public ModelAndView testView(@RequestParam("lineName") String lineName,
                                 @RequestParam("lineLink") String lineLink,
                                 @RequestParam("lineImg") String lineImg,
                                 @RequestParam("priority") Integer priority){
        HeadLine headLine = new HeadLine();
        headLine.setPriority(priority);
        headLine.setLineName(lineName);
        headLine.setLineLink(lineLink);
        headLine.setLineImg(lineImg);

        Result<Boolean> result = this.headLineShopCategoryCombineService.addHeadLine(headLine);

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setView("addheadline.jsp").addViewData("result", result);
        return modelAndView;
    }
}
