package com.sky.takeout.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderCancelDTO implements Serializable {
    //订单id
    private Long id;
    //取消原因
    private String cancelReason;
}
