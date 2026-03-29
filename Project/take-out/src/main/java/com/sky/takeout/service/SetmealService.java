package com.sky.takeout.service;

import com.sky.takeout.dto.SetmealDTO;
import com.sky.takeout.dto.SetmealPageQueryDTO;
import com.sky.takeout.entity.Dish;
import com.sky.takeout.entity.Setmeal;
import com.sky.takeout.result.PageResult;

import java.util.List;

public interface SetmealService{
    void saveWithDish(SetmealDTO setmealDTO);
    PageResult pageQuery(SetmealPageQueryDTO dto);
    void deleteBatch(List<Long> ids);
    SetmealDTO getByIdWithDish(Long id);
    void updateWithDish(SetmealDTO dto);
    void updateStatus(Integer status, Long id);
    List<SetmealDTO> listByCategoryId(Long categoryId);
}
