package com.sky.takeout.service;

import com.sky.takeout.dto.ShoppingCartDTO;
import com.sky.takeout.entity.ShoppingCart;

import java.util.List;

public interface ShoppingCartService {
    //添加商品到购物车
    ShoppingCart add(ShoppingCartDTO shoppingCartDTO);

    //查看我的购物车
    List<ShoppingCart> list();

    //清空我的购物车
    void clean();
}
