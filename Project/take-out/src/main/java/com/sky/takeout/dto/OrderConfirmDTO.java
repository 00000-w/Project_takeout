package com.sky.takeout.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class OrderConfirmDTO implements Serializable {
    //订单id
    private Long id;
    //目标状态（接单/拒单)
    private Integer status;
}
