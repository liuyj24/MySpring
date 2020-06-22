package com.liuyj.service.combine.impl;

import com.liuyj.entity.bo.HeadLine;
import com.liuyj.entity.bo.ShopCategory;
import com.liuyj.entity.dto.MainPageInfoDTO;
import com.liuyj.entity.dto.Result;
import com.liuyj.service.combine.HeadLineShopCategoryCombineService;
import com.liuyj.service.solo.HeadLineService;
import com.liuyj.service.solo.ShopCategoryService;
import org.simpleframework.core.annotation.Service;

import java.util.List;

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

    private Result<MainPageInfoDTO> mergeMainPageInfoDTO(Result<List<HeadLine>> headLineResult, Result<List<ShopCategory>> shopCategoryResult) {
        return null;
    }
}
