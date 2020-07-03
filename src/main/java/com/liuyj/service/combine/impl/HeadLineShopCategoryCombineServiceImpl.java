package com.liuyj.service.combine.impl;

import com.liuyj.entity.bo.HeadLine;
import com.liuyj.entity.bo.ShopCategory;
import com.liuyj.entity.dto.MainPageInfoDTO;
import com.liuyj.entity.dto.Result;
import com.liuyj.service.combine.HeadLineShopCategoryCombineService;
import com.liuyj.service.solo.HeadLineService;
import com.liuyj.service.solo.ShopCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.simpleframework.core.annotation.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class HeadLineShopCategoryCombineServiceImpl implements HeadLineShopCategoryCombineService {

    private HeadLineService headLineService;

    private ShopCategoryService shopCategoryService;

    @Override
    public Result<MainPageInfoDTO> getMainPageInfo() {
        HeadLine headLine = new HeadLine();
        headLine.setEnableStatus(1);
        Result<List<HeadLine>> headLineResult = headLineService.queryHeadLine(headLine, 1, 10);

        ShopCategory shopCategory = new ShopCategory();
        Result<List<ShopCategory>> shopCategoryResult = shopCategoryService.queryShopCategory(shopCategory, 1, 10);

        Result<MainPageInfoDTO> result = mergeMainPageInfoDTO(headLineResult, shopCategoryResult);
        return result;
    }

    @Override
    public Result<List<HeadLine>> queryAll() {
        Result<List<HeadLine>> result = new Result<>();
        List<HeadLine> resultList = new ArrayList<>();

        HeadLine headLine1 = new HeadLine();
        headLine1.setEnableStatus(1);
        headLine1.setCreateTime(new Date());
        headLine1.setLastEditTime(new Date());
        headLine1.setLineId(1l);
        headLine1.setLineImg("www.baidu.com");
        headLine1.setLineLink("www.baidu.com");
        headLine1.setLineName("headline1");
        headLine1.setPriority(1);

        resultList.add(headLine1);

        result.setData(resultList);
        result.setCode(200);
        result.setMsg("ok");

        return result;
    }

    @Override
    public Result<Boolean> addHeadLine(HeadLine headLine) {
        log.info("addHeadLine被执行了");
        Result<Boolean> result = new Result<>();
        result.setCode(200);
        result.setData(true);
        result.setMsg("ok");
        return result;
    }

    private Result<MainPageInfoDTO> mergeMainPageInfoDTO(Result<List<HeadLine>> headLineResult, Result<List<ShopCategory>> shopCategoryResult) {
        return null;
    }
}
