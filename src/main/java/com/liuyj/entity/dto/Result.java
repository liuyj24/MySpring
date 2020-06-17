package com.liuyj.entity.dto;

import lombok.Data;

/**
 * 封装Controller与Service之间的结果集
 */
@Data
public class Result<T> {
    //本次请求结果的状态码，200表示成功
    private int code;
    //请求结果描述
    private String msg;
    //请求返回数据
    private T data;
}
