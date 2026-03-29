package com.sky.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.takeout.entity.OrderDetail;
import com.sky.takeout.entity.Orders;
import lombok.Data;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderDetailMapper extends BaseMapper<OrderDetail> {
    //根据订单ID查询订单明细
    @Select("SELECT * FROM order_detail where order_id = #{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
