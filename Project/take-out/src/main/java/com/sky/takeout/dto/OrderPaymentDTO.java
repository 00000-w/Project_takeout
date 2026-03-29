package com.sky.takeout.dto;

import com.sky.takeout.constant.OrderConstant;
import lombok.Data;

import java.io.Serializable;

@Data
public class OrderPaymentDTO implements Serializable {
    //订单号
    private String orderNumber;
    //支付方式（默认微信支付）
    private Integer payment = OrderConstant.WECHAT_PAY;
    //模拟支付成功，无需其他参数
}
