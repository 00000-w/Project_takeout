package com.sky.takeout.controller;

import com.sky.takeout.dto.*;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.result.Result;
import com.sky.takeout.service.OrderService;
import com.sky.takeout.vo.OrderVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 管理端订单接口
 */
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    // 管理端订单分页查询
    @GetMapping("/conditionSearch")
    public Result<PageResult> conditionSearch(OrderPageQueryDTO orderPageQueryDTO) {
        PageResult pageResult = orderService.pageQuery(orderPageQueryDTO);
        return Result.success(pageResult);
    }

    // 查询订单详情
    @GetMapping("/details/{id}")
    public Result<OrderVO> details(@PathVariable Long id) {
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    // 接单
    @PutMapping("/confirm")
    public Result<Void> confirm(@RequestBody OrderConfirmDTO dto) {
        orderService.confirm(dto);
        return Result.success();
    }

    // 拒单
    @PutMapping("/rejection")
    public Result<Void> rejection(@RequestBody OrderCancelDTO dto) {
        orderService.rejection(dto);
        return Result.success();
    }

    // 取消订单（管理端）
    @PutMapping("/cancel")
    public Result<Void> cancel(@RequestBody OrderCancelDTO dto) {
        orderService.cancel(dto);
        return Result.success();
    }

    // 派送
    @PutMapping("/delivery/{id}")
    public Result<Void> delivery(@PathVariable Long id) {
        orderService.delivery(id);
        return Result.success();
    }

    // 完成
    @PutMapping("/complete/{id}")
    public Result<Void> complete(@PathVariable Long id) {
        orderService.complete(id);
        return Result.success();
    }
}
