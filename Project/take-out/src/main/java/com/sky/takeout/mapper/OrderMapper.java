package com.sky.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.takeout.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper extends BaseMapper<Orders> {
    //BaseMapper已提供基本CRUD

    @Select("SELECT * FROM `order` WHERE number = #{orderNumber}")
    Orders getByOrderNumber(String orderNumber);
}
