package com.sky.takeout.service;

import com.sky.takeout.dto.*;
import com.sky.takeout.entity.OrderDetail;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.vo.OrderSubmitVO;
import com.sky.takeout.vo.OrderVO;

import java.util.Map;

public interface OrderService {
    //用户下单
    OrderSubmitVO submitOrder(OrderSubmitDTO orderSubmitDTO);

    //订单分页查询（管理员）
    PageResult pageQuery(OrderPageQueryDTO orderPageQueryDTO);

    //订单状态管理
    //1.接单
    void confirm(OrderConfirmDTO orderConfirmDTO);
    //2.拒单
    void rejection(OrderCancelDTO orderCancelDTO);
    //3.取消订单
    void cancel(OrderCancelDTO orderCancelDTO);
    //4.派送
    void delivery(Long id);
    //5.完成
    void complete(Long id);

    //查询订单详情
    OrderVO getOrderDetail(Long id);

    //用户查看历史订单
    PageResult getHistoryOrders(OrderPageQueryDTO dto);

    //支付功能
    void payment(OrderPaymentDTO dto);

    //获取支付状态
    Map<String, Object> getPaymentStatus(String orderNumber);
}
