package com.sky.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sky.takeout.dto.ShoppingCartDTO;
import com.sky.takeout.entity.Dish;
import com.sky.takeout.entity.Setmeal;
import com.sky.takeout.entity.ShoppingCart;
import com.sky.takeout.mapper.DishMapper;
import com.sky.takeout.mapper.SetmealMapper;
import com.sky.takeout.mapper.ShoppingCartMapper;
import com.sky.takeout.service.ShoppingCartService;
import org.apache.ibatis.annotations.Select;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private SetmealMapper setmealMapper;

    @Override
    @Transactional
    public ShoppingCart add(ShoppingCartDTO shoppingCartDTO) {
        //模拟用户id
        Long currentUserId = 1L;

        //将dto属性拷贝到entity
        ShoppingCart shoppingCart = new ShoppingCart();
        BeanUtils.copyProperties(shoppingCartDTO, shoppingCart);
        shoppingCart.setUserId(currentUserId);

        //构建查询条件，检查购物车是否已有该商品
        ShoppingCart cartInDB = shoppingCartMapper.selectOne(
                new LambdaQueryWrapper<ShoppingCart>()
                        //必须是当前用户的
                        .eq(ShoppingCart::getUserId, currentUserId)
                        //dishId不为空才作为条件
                        .eq(shoppingCartDTO.getDishId() != null, ShoppingCart::getDishId, shoppingCartDTO.getDishId())
                        //setmealId不为空才为条件
                        .eq(shoppingCartDTO.getSetmealId() != null, ShoppingCart::getSetmealId, shoppingCartDTO.getSetmealId())
                        //口味也要相同
                        .eq(ShoppingCart::getDishFlavor, shoppingCartDTO.getDishFlavor())
        );

        //如果已存在，数量+1
        if (cartInDB != null) {
            cartInDB.setNumber(cartInDB.getNumber() + 1);
            shoppingCartMapper.updateById(cartInDB);
            return cartInDB;
        }

        //如果不存在，添加新记录
        if (shoppingCartDTO.getDishId() != null) {
            Dish dish = dishMapper.selectById(shoppingCartDTO.getDishId());
            shoppingCart.setName(dish.getName());
            shoppingCart.setImage(dish.getImage());
            shoppingCart.setAmount(dish.getPrice());
        } else if (shoppingCartDTO.getSetmealId() != null) {
            Setmeal setmeal = setmealMapper.selectById(shoppingCartDTO.getSetmealId());
            shoppingCart.setName(setmeal.getName());
            shoppingCart.setImage(setmeal.getImage());
            shoppingCart.setAmount(setmeal.getPrice());
        }

        shoppingCart.setNumber(1);
        shoppingCart.setCreateTime(LocalDateTime.now());
        shoppingCartMapper.insert(shoppingCart);
        return shoppingCart;
    }

    @Override
    public List<ShoppingCart> list() {
        Long currentUserId = 1L;

        return shoppingCartMapper.getByUserId(currentUserId);
    }

    @Override
    @Transactional
    public void clean() {
        Long currentUserId = 1L;

        shoppingCartMapper.cleanByUserId(currentUserId);
    }
}
