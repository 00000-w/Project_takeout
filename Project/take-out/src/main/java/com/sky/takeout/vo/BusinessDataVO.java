package com.sky.takeout.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessDataVO implements Serializable {
    //今日营业额
    private Double turnover;

    //今日有效订单数
    private Integer validOrderCount;

    //今日新增用户数
    private Integer newUsers;

    //订单完成率
    private Double orderCompletionRate;

    //平均客单价（营业额/有效订单数）
    private Double unitPrice;
}
