package com.liuyj.entity.bo;

import lombok.Data;

import java.util.Date;

/**
 * 商店类目实体类
 */
@Data
public class ShopCategory {
    private Long shopCategoryId;
    private String shopCategoryName;
    private String shopCategoryDesc;
    private String shopCategoryImg;
    private Integer priority;
    private Date createTime;
    private Date lastEditTime;
    private ShopCategory parent;
}
