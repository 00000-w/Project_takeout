package com.sky.takeout.service;

import com.sky.takeout.vo.OrderReportVO;
import com.sky.takeout.vo.SalesTop10ReportVO;
import com.sky.takeout.vo.TurnoverReportVO;
import com.sky.takeout.vo.UserReportVO;

import java.time.LocalDate;

public interface ReportService {
    //营业额统计
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    //用户统计
    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    //订单统计
    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    //销量Top10
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);
}
