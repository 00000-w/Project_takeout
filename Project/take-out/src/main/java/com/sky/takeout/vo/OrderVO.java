package com.sky.takeout.vo;

import com.sky.takeout.entity.OrderDetail;
import lombok.Data;
import org.springframework.cglib.core.Local;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.lang.module.Configuration;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Data
public class OrderVO implements Serializable {
    private Long id;
    private Integer status;
    private String statusDesc;
    private Long userId;
    private Long addressBookId;
    private LocalDateTime orderTime;
    private LocalDateTime checkoutTime;
    private Integer payMethod;
    private String payMethodDesc;
    private Integer payStatus;
    private BigDecimal amount;
    private String remark;
    private String phone;
    private String address;
    private String userName;
    private String consignee;

    //订单明细列表
    private List<OrderDetail> orderDetails;

    //计算商品总数量
    public Integer getTotalNum() {
        if (CollectionUtils.isEmpty(orderDetails)) {
            return 0;
        }
        return orderDetails.stream()
                .mapToInt(OrderDetail::getNumber)
                .sum();
    }
}
