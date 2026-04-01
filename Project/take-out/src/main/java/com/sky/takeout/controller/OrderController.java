package com.sky.takeout.controller;

import com.sky.takeout.dto.*;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.result.Result;
import com.sky.takeout.service.OrderService;
import com.sky.takeout.vo.OrderSubmitVO;
import com.sky.takeout.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户端订单接口
 */
@RestController
@RequestMapping("/user/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 用户下单
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrderSubmitDTO orderSubmitDTO) {
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(orderSubmitDTO);
        return Result.success(orderSubmitVO);
    }

    // 用户查看历史订单
    @GetMapping("/historyOrders")
    public Result<PageResult> historyOrders(OrderPageQueryDTO dto) {
        PageResult pageResult = orderService.getHistoryOrders(dto);
        return Result.success(pageResult);
    }

    // 用户查看订单详情
    @GetMapping("/detail/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    // 用户支付
    @PutMapping("/payment")
    public Result<Void> payment(@RequestBody OrderPaymentDTO dto) {
        orderService.payment(dto);
        return Result.success();
    }

    // 查询支付状态
    @GetMapping("/payment/status")
    public Result<Map<String, Object>> getPaymentStatus(@RequestParam String orderNumber) {
        Map<String, Object> paymentStatus = orderService.getPaymentStatus(orderNumber);
        return Result.success(paymentStatus);
    }

    // 用户取消订单
    @PutMapping("/cancel")
    public Result<Void> cancel(@RequestBody OrderCancelDTO dto) {
        orderService.cancel(dto);
        return Result.success();
    }
}
