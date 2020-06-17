package com.liuyj.entity.dto;

import com.liuyj.entity.bo.HeadLine;
import com.liuyj.entity.bo.ShopCategory;
import lombok.Data;

import java.util.List;

@Data
public class MainPageInfoDTO {
    private List<HeadLine> headLineList;
    private List<ShopCategory> shopCategoryList;
}
