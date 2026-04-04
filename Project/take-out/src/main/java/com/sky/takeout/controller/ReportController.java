package com.sky.takeout.controller;

import com.sky.takeout.result.Result;
import com.sky.takeout.service.ReportService;
import com.sky.takeout.vo.OrderReportVO;
import com.sky.takeout.vo.SalesTop10ReportVO;
import com.sky.takeout.vo.TurnoverReportVO;
import com.sky.takeout.vo.UserReportVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("admin/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/turnoverStatistics")
    public Result<TurnoverReportVO> turnoverStatistics(// @DateTimeFormat 告诉Spring如何把字符串转成LocalDate
                                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end){
        log.info("营业额统计， begin：{}, end：{}", begin, end);
        TurnoverReportVO turnoverStatistics = reportService.getTurnoverStatistics(begin, end);
        return Result.success(turnoverStatistics);
    }

    @GetMapping("/userStatistics")
    public Result<UserReportVO> userStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {
        log.info("用户统计，begin：{}, end：{}", begin, end);
        return Result.success(reportService.getUserStatistics(begin, end));
    }

    @GetMapping("/orderStatistics")
    public Result<OrderReportVO> orderStatistics(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
                                                 @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end) {

        log.info("订单统计, begin:{}, end:{}", begin, end);
        return Result.success(reportService.getOrdersStatistics(begin, end));
    }

    @GetMapping("/top10")
    public Result<SalesTop10ReportVO> top10(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate begin,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate end
    ) {
        log.info("销量前10，begin:{}, end:{}", begin, end);
        return Result.success(reportService.getSalesTop10(begin, end));
    }
}
