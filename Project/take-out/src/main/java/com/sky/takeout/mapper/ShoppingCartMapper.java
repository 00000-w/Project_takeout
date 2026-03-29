package com.sky.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.sky.takeout.dto.ShoppingCartDTO;
import com.sky.takeout.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {
    //根据用户id查询购物车
    @Select("SELECT * FROM shopping_cart WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<ShoppingCart> getByUserId(Long userId);

    //清空用户购物车
    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    void cleanByUserId(Long userId);
}
