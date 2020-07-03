package com.liuyj.service.combine;


import com.liuyj.entity.bo.HeadLine;
import com.liuyj.entity.dto.MainPageInfoDTO;
import com.liuyj.entity.dto.Result;

import java.util.List;

public interface HeadLineShopCategoryCombineService {
    Result<MainPageInfoDTO> getMainPageInfo();

    Result<List<HeadLine>> queryAll();

    Result<Boolean> addHeadLine(HeadLine headLine);
}
