package com.sky.takeout.constant;

public class OrderConstant {
    //订单状态
    public static final int PENDING_PAYMENT = 1;
    public static final int TO_BE_CONFIRMED = 2;
    public static final int CONFIRMED = 3;
    public static final int IN_DELIVERY_PROGRESS = 4;
    public static final int COMPLETED = 5;
    public static final int CANCELLED = 6;
    public static final int REFUNDED = 7;

    //支付状态
    public static final int UN_PAID = 0;
    public static final int PAID = 1;
    public static final int REFUND = 2;

    //支付方式
    public static final int WECHAT_PAY = 1;
    public static final int ALIPAY = 2;
}
