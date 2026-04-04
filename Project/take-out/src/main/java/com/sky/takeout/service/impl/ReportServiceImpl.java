package com.sky.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.takeout.constant.OrderConstant;
import com.sky.takeout.entity.OrderDetail;
import com.sky.takeout.entity.Orders;
import com.sky.takeout.entity.User;
import com.sky.takeout.mapper.OrderDetailMapper;
import com.sky.takeout.mapper.OrderMapper;
import com.sky.takeout.mapper.UserMapper;
import com.sky.takeout.service.ReportService;
import com.sky.takeout.vo.OrderReportVO;
import com.sky.takeout.vo.SalesTop10ReportVO;
import com.sky.takeout.vo.TurnoverReportVO;
import com.sky.takeout.vo.UserReportVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportServiceImpl implements ReportService {

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private OrderDetailMapper orderDetailMapper;

    @Autowired
    private UserMapper userMapper;


    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        // 1. 生成日期列表
        List<LocalDate> dateList = getDateList(begin, end);

        // 2. 查询每天营业额
        List<String> turnoverList = dateList.stream()
                .map(date -> {
                    // 当天开始和结束时间
                    LocalDateTime dayBegin = LocalDateTime.of(date, LocalTime.MIN);
                    LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

                    // 查询当天已完成的订单金额总和
                    LambdaQueryWrapper<Orders> qw = new LambdaQueryWrapper<>();
                    qw.eq(Orders::getStatus, OrderConstant.COMPLETED);
                    qw.between(Orders::getOrderTime, dayBegin, dayEnd);

                    List<Orders> orders = orderMapper.selectList(qw);

                    // 计算总金额，没有订单则为0
                    BigDecimal turnover = orders.stream()
                            .map(Orders::getAmount)
                            .filter(Objects::nonNull)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return turnover.toString();
                })
                .collect(Collectors.toList());

        // 3. 封装返回
        return TurnoverReportVO.builder()
                .dateList(String.join(",", dateList.stream()
                        .map(LocalDate::toString)
                        .collect(Collectors.toList())))
                .turnoverList(String.join(",", turnoverList))
                .build();
    }

    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        List<String> newUserList = new ArrayList<>();
        List<String> totalUserList = new ArrayList<>();

        for (LocalDate date : dateList) {
            LocalDateTime dayBegin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            // 当天新增用户数
            Long newUsers = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .between(User::getCreateTime, dayBegin, dayEnd)
            );
            newUserList.add(newUsers.toString());

            // 截止当天的累计用户总量
            Long totalUsers = userMapper.selectCount(
                    new LambdaQueryWrapper<User>()
                            .le(User::getCreateTime, dayEnd)
            );
            totalUserList.add(totalUsers.toString());
        }

        return UserReportVO.builder()
                .dateList(String.join(",", dateList.stream()
                        .map(LocalDate::toString)
                        .collect(Collectors.toList())))
                .newUserList(String.join(",", newUserList))
                .totalUserList(String.join(",", totalUserList))
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = getDateList(begin, end);

        List<String> orderCountList = new ArrayList<>();
        List<String> validOrderCountList = new ArrayList<>();

        long totalOrderCount = 0;
        long totalValidOrderCount = 0;

        for (LocalDate date : dateList) {
            LocalDateTime dayBegin = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime dayEnd = LocalDateTime.of(date, LocalTime.MAX);

            // 当天总订单数
            Long orderCount = orderMapper.selectCount(
                    new LambdaQueryWrapper<Orders>()
                            .between(Orders::getOrderTime, dayBegin, dayEnd)
            );
            orderCountList.add(orderCount.toString());
            totalOrderCount += orderCount;

            // 当天有效订单数（已完成）
            Long validOrderCount = orderMapper.selectCount(
                    new LambdaQueryWrapper<Orders>()
                            .eq(Orders::getStatus, OrderConstant.COMPLETED)
                            .between(Orders::getOrderTime, dayBegin, dayEnd)
            );
            validOrderCountList.add(validOrderCount.toString());
            totalValidOrderCount += validOrderCount;
        }

        // 计算完成率
        double completionRate = totalOrderCount == 0 ? 0.0
                : (double) totalValidOrderCount / totalOrderCount;

        return OrderReportVO.builder()
                .dateList(String.join(",", dateList.stream()
                        .map(LocalDate::toString)
                        .collect(Collectors.toList())))
                .orderCountList(String.join(",", orderCountList))
                .validOrderCountList(String.join(",", validOrderCountList))
                .totalOrderCount(totalOrderCount)
                .totalValidOrderCount(totalValidOrderCount)
                .orderCompletionRate(completionRate)
                .build();
    }


    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {
        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);

        List<Orders> completedOrders = orderMapper.selectList(new LambdaQueryWrapper<Orders>()
                .between(Orders::getOrderTime, beginTime, endTime)
                .eq(Orders::getStatus, OrderConstant.COMPLETED));

        if (completedOrders.isEmpty()) {
            return SalesTop10ReportVO.builder()
                    .nameList("")
                    .numberList("")
                    .build();
        }

        List<Long> orderIds = completedOrders.stream()
                .map(Orders::getId)
                .toList();  //toList()返回不可变集合

        List<OrderDetail> orderDetails = orderDetailMapper.selectList(
                new LambdaQueryWrapper<OrderDetail>()
                        .in(OrderDetail::getId, orderIds)
        );

        Map<String, Integer> salesMap = orderDetails.stream()
                .collect(Collectors.groupingBy(
                        OrderDetail::getName,
                        Collectors.summingInt(OrderDetail::getNumber)
                ));

        List<Map.Entry<String, Integer>> top10 = salesMap.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(10)
                .collect(Collectors.toList());

        List<String> nameList = top10.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        List<String> numberList = top10.stream()
                .map(e -> e.getValue().toString())
                .collect(Collectors.toList());


        return SalesTop10ReportVO.builder()
                .nameList(String.join(",", nameList))
                .numberList(String.join(",", numberList))
                .build();
    }
    private List<LocalDate> getDateList(LocalDate begin, LocalDate end) {
        List<LocalDate> dateList = new ArrayList<>();
        LocalDate current = begin;
        while (!current.isAfter(end)) {
            dateList.add(current);
            current = current.plusDays(1);
        }
        return dateList;
    }
}
