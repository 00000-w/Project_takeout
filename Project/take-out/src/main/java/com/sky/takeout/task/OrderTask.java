package com.sky.takeout.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.takeout.constant.OrderConstant;
import com.sky.takeout.entity.Orders;
import com.sky.takeout.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单定时任务
 * 处理异常状态的订单
 */
@Slf4j
@Component
public class OrderTask {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 处理超时未支付订单
     * 每分钟执行一次
     * 下单超过15分钟未支付 → 自动取消
     */
    @Scheduled(cron = "0 * * * * ?")  // 每分钟的第0秒触发
    public void processTimeoutOrder() {
        log.info("定时任务执行：处理超时未支付订单，时间：{}", LocalDateTime.now());

        // 查询超时未支付订单
        // 条件：状态为待付款 且 下单时间超过15分钟
        LocalDateTime timeoutTime = LocalDateTime.now().minusMinutes(15);

        List<Orders> timeoutOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, OrderConstant.PENDING_PAYMENT)
                        // 下单时间 < 当前时间-15分钟 = 超时
                        .lt(Orders::getOrderTime, timeoutTime)
        );

        if (timeoutOrders.isEmpty()) {
            log.info("暂无超时未支付订单");
            return;
        }

        log.info("发现{}笔超时未支付订单，开始自动取消", timeoutOrders.size());

        // 批量取消
        for (Orders order : timeoutOrders) {
            Orders cancelOrder = Orders.builder()
                    .id(order.getId())
                    .status(OrderConstant.CANCELLED)
                    .build();
            orderMapper.updateById(cancelOrder);
            log.info("订单{}已自动取消（超时未支付）", order.getNumber());
        }
    }

    /**
     * 处理派送中超时订单
     * 每天凌晨1点执行
     * 派送超过1小时还未完成 → 自动完成
     */
    @Scheduled(cron = "0 0 1 * * ?")  // 每天凌晨1点触发
    public void processDeliveryOrder() {
        log.info("定时任务执行：处理派送中超时订单，时间：{}", LocalDateTime.now());

        // 查询超时派送中的订单
        // 条件：状态为派送中 且 下单时间超过1小时
        LocalDateTime timeoutTime = LocalDateTime.now().minusHours(1);

        List<Orders> deliveryOrders = orderMapper.selectList(
                new LambdaQueryWrapper<Orders>()
                        .eq(Orders::getStatus, OrderConstant.IN_DELIVERY_PROGRESS)
                        .lt(Orders::getOrderTime, timeoutTime)
        );

        if (deliveryOrders.isEmpty()) {
            log.info("暂无派送超时订单");
            return;
        }

        log.info("发现{}笔派送超时订单，开始自动完成", deliveryOrders.size());

        for (Orders order : deliveryOrders) {
            Orders completeOrder = Orders.builder()
                    .id(order.getId())
                    .status(OrderConstant.COMPLETED)
                    .checkoutTime(LocalDateTime.now())
                    .build();
            orderMapper.updateById(completeOrder);
            log.info("订单{}已自动完成（派送超时）", order.getNumber());
        }
    }
}
