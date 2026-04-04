package com.sky.takeout.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderReportVO implements Serializable {
    private String dateList;
    private String orderCountList;
    private String validOrderCountList;
    //订单总数
    private Long totalOrderCount;
    //有效订单总数
    private Long totalValidOrderCount;
    //订单完成率
    private Double orderCompletionRate;
}
