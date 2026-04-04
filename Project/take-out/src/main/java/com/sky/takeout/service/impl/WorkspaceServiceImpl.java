package com.sky.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.takeout.constant.OrderConstant;
import com.sky.takeout.entity.Dish;
import com.sky.takeout.entity.Orders;
import com.sky.takeout.entity.Setmeal;
import com.sky.takeout.entity.User;
import com.sky.takeout.mapper.DishMapper;
import com.sky.takeout.mapper.OrderMapper;
import com.sky.takeout.mapper.SetmealMapper;
import com.sky.takeout.mapper.UserMapper;
import com.sky.takeout.service.WorkspaceService;
import com.sky.takeout.vo.BusinessDataVO;
import com.sky.takeout.vo.DishOverViewVO;
import com.sky.takeout.vo.OrderOverViewVO;
import com.sky.takeout.vo.SetmealOverViewVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Objects;

@Service
public class WorkspaceServiceImpl implements WorkspaceService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    public BusinessDataVO getBusinessData(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        // 1.今日营业额（已完成订单金额总和）
        List<Orders> completedOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, OrderConstant.COMPLETED)
                        .between(Orders::getOrderTime, beginTime, endTime)
        );

        Double turnover = completedOrders.stream()
                .map(Orders::getAmount)
                .filter(Objects::nonNull)
                .map(BigDecimal::doubleValue)
                .reduce(0.0, Double::sum);

        // 2.今日有效订单数
        Integer validOrderCount = completedOrders.size();

        // 3.今日新增用户数
        Long newUsers = userMapper.selectCount(
                new LambdaQueryWrapper<User>()
                        .between(User::getCreateTime, beginTime, endTime)
        );

        // 4.今日总订单数
        Long totalOrderCount = orderMapper.selectCount(
                new LambdaQueryWrapper<Orders>()
                        .between(Orders::getOrderTime, beginTime, endTime)
        );

        // 5.订单完成率
        double orderCompletionRate = totalOrderCount == 0 ? 0.0
                : (double) validOrderCount / totalOrderCount;

        // 6.平均客单价
        double unitPrice = validOrderCount == 0 ? 0.0
                : turnover / validOrderCount;

        return BusinessDataVO.builder()
                .turnover(turnover)
                .validOrderCount(validOrderCount)
                .newUsers(newUsers.intValue())
                .orderCompletionRate(orderCompletionRate)
                .unitPrice(unitPrice)
                .build();
    }

    @Override
    public OrderOverViewVO getOrderOverView() {
        // 待接单
        Long waitingOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, OrderConstant.TO_BE_CONFIRMED)
        );

        // 待派送
        Long deliveredOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, OrderConstant.CONFIRMED)
        );

        // 已完成
        Long completedOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, OrderConstant.COMPLETED)
        );

        // 已取消
        Long cancelledOrders = orderMapper.selectCount(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, OrderConstant.CANCELLED)
        );

        // 全部订单
        Long allOrders = orderMapper.selectCount(null);

        return OrderOverViewVO.builder()
                .waitingOrders(waitingOrders.intValue())
                .deliveredOrders(deliveredOrders.intValue())
                .completedOrders(completedOrders.intValue())
                .cancelledOrders(cancelledOrders.intValue())
                .allOrders(allOrders.intValue())
                .build();
    }

    @Override
    public DishOverViewVO getDishOverView() {
        // 已起售
        Long sold = dishMapper.selectCount(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getStatus, 1)
        );

        // 已停售
        Long discontinued = dishMapper.selectCount(
                new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getStatus, 0)
        );

        return DishOverViewVO.builder()
                .sold(sold.intValue())
                .discontinued(discontinued.intValue())
                .build();
    }

    @Override
    public SetmealOverViewVO getSetmealOverView() {
        // 已起售
        Long sold = setmealMapper.selectCount(
                new LambdaQueryWrapper<Setmeal>()
                        .eq(Setmeal::getStatus, 1)
        );

        // 已停售
        Long discontinued = setmealMapper.selectCount(
                new LambdaQueryWrapper<Setmeal>()
                        .eq(Setmeal::getStatus, 0)
        );

        return SetmealOverViewVO.builder()
                .sold(sold.intValue())
                .discontinued(discontinued.intValue())
                .build();
    }
}
