package com.sky.takeout.service;

import com.sky.takeout.vo.BusinessDataVO;
import com.sky.takeout.vo.DishOverViewVO;
import com.sky.takeout.vo.OrderOverViewVO;
import com.sky.takeout.vo.SetmealOverViewVO;

import java.time.LocalDate;

public interface WorkspaceService {
    //查询今日数据
    BusinessDataVO getBusinessData(LocalDate begin, LocalDate end);

    //查询订单管理数据
    OrderOverViewVO getOrderOverView();

    //查询菜品总览
    DishOverViewVO getDishOverView();

    //查询套餐总览
    SetmealOverViewVO getSetmealOverView();
}
