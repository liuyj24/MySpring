package com.liuyj.entity.bo;

import lombok.Data;

import java.util.Date;

/**
 * 商店首页头条实体类
 */
@Data
public class HeadLine {
    private Long lineId;
    private String lineName;
    private String lineLink;
    private String lineImg;
    private Integer priority;
    private Integer enableStatus;
    private Date createTime;
    private Date lastEditTime;

}
