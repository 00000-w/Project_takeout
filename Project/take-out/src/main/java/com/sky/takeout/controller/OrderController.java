package com.sky.takeout.controller;

import com.sky.takeout.dto.*;
import com.sky.takeout.mapper.OrderMapper;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.result.Result;
import com.sky.takeout.service.OrderService;
import com.sky.takeout.vo.OrderSubmitVO;
import com.sky.takeout.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrderSubmitDTO orderSubmitDTO) {
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(orderSubmitDTO);

        return Result.success(orderSubmitVO);
    }

    @GetMapping("/page")
    public Result<PageResult> page(OrderPageQueryDTO orderPageQueryDTO) {
        PageResult pageResult = orderService.pageQuery(orderPageQueryDTO);

        return Result.success(pageResult);
    }

    @PutMapping("/confirm")
    public Result<Void> confirm(@RequestBody OrderConfirmDTO dto) {
        orderService.confirm(dto);

        return Result.success();
    }

    @PutMapping("/rejection")
    public Result<Void> rejection(@RequestBody OrderCancelDTO dto) {
        orderService.rejection(dto);

        return Result.success();
    }

    @PutMapping("/cancel")
    public Result<Void> cancel(@RequestBody OrderCancelDTO dto) {
        orderService.cancel(dto);

        return Result.success();
    }

    @PutMapping("/delivery/{id}")
    public Result<Void> delivery(@PathVariable Long id) {
        orderService.delivery(id);

        return Result.success();
    }

    @PutMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Long id) {
        orderService.complete(id);

        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result<OrderVO> detail(@PathVariable Long id) {
        OrderVO orderDetail = orderService.getOrderDetail(id);

        return Result.success(orderDetail);
    }

    @GetMapping("/historyOrders")
    public Result<PageResult> pageHistoryOrder(OrderPageQueryDTO dto) {
        PageResult pageResult = orderService.getHistoryOrders(dto);

        return Result.success(pageResult);
    }

    @PutMapping("/payment")
    public Result<Void> payment(@RequestBody OrderPaymentDTO dto) {
        orderService.payment(dto);

        return Result.success();
    }

    @GetMapping("/payment/status")
    public Result<Map<String, Object>> getPaymentStatus(@RequestParam String orderNumber) {
        Map<String, Object> paymentStatus = orderService.getPaymentStatus(orderNumber);

        return Result.success(paymentStatus);
    }
}
