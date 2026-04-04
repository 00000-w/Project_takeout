package com.sky.takeout.controller;

import com.sky.takeout.result.Result;
import com.sky.takeout.service.WorkspaceService;
import com.sky.takeout.vo.BusinessDataVO;
import com.sky.takeout.vo.DishOverViewVO;
import com.sky.takeout.vo.OrderOverViewVO;
import com.sky.takeout.vo.SetmealOverViewVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/admin/workspace")
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @GetMapping("/businessData")
    public Result<BusinessDataVO> businessData() {
        LocalDate today = LocalDate.now();
        log.info("查询工作台今日数据，日期：{}", today);
        BusinessDataVO vo = workspaceService.getBusinessData(today, today);
        return Result.success(vo);
    }

    @GetMapping("/overviewOrders")
    public Result<OrderOverViewVO> overviewOrders() {
        log.info("查询订单管理数据");
        OrderOverViewVO vo = workspaceService.getOrderOverView();
        return Result.success(vo);
    }

    @GetMapping("/overviewDishes")
    public Result<DishOverViewVO> overviewDishes() {
        log.info("查询菜品总览数据");
        DishOverViewVO vo = workspaceService.getDishOverView();
        return Result.success(vo);
    }

    @GetMapping("/overviewSetmeals")
    public Result<SetmealOverViewVO> overviewSetmeals() {
        log.info("查询套餐总览数据");
        SetmealOverViewVO vo = workspaceService.getSetmealOverView();
        return Result.success(vo);
    }
}
