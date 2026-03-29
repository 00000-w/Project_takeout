package com.sky.takeout.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderPageQueryDTO {
    private Integer page;
    private Integer pageSize;

    //订单号
    //手机号
    //订单状态
    //开始时间
    //结束时间
    private String number;
    private String phone;
    private Integer status;
    private LocalDateTime beginTime;
    private LocalDateTime endTime;

}
