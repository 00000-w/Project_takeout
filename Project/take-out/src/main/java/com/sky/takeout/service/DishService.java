package com.sky.takeout.service;

import com.sky.takeout.dto.DishDTO;
import com.sky.takeout.dto.DishPageQueryDTO;
import com.sky.takeout.result.PageResult;

import java.util.List;

public interface DishService {
    //新增菜品和口味
    void saveWithFlavor(DishDTO dto);

    //菜品分页查询
    PageResult pageQuery(DishPageQueryDTO dto);

    //根据id查询菜品和口味
    DishDTO getByIdWithFlavor(Long id);

    //修改菜品和口味
    void updateWithFlavor(DishDTO dto);

    //批量删除菜品
    void deleteBatch(List<Long> ids);

    //菜品起售停售
    void updateStatus(Long id, Integer status);

    //根据分类id查询菜品
    List<DishDTO> listByCategoryId(Long categoryId);
}
