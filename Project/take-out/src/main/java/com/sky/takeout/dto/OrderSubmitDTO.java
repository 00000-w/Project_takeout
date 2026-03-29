package com.sky.takeout.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
public class OrderSubmitDTO implements Serializable {
    //收货地址
    private Long addressBookId;
    //付款方式
    private int payMethod = 1;
    //备注
    private String remark;
}
