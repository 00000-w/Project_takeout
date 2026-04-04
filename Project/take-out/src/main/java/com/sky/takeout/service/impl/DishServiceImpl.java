package com.sky.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.takeout.dto.DishDTO;
import com.sky.takeout.dto.DishPageQueryDTO;
import com.sky.takeout.entity.Dish;
import com.sky.takeout.entity.DishFlavor;
import com.sky.takeout.mapper.DishFlavorMapper;
import com.sky.takeout.mapper.DishMapper;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;

    @Autowired
    private DishFlavorMapper dishFlavorMapper;

    @Override
    @CacheEvict(value = "dish", allEntries = true)
    public void saveWithFlavor(DishDTO dto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);
        //默认起售
        dish.setStatus(1);
        dishMapper.insert(dish);

        Long dishId = dish.getId();

        List<DishFlavor> flavors = dto.getFlavors();
        if (dishId != null && flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> flavor.setDishId(dishId));
            for (DishFlavor flavor : flavors) {
                //犯错:写了dishFlavorMapper.insert(dto.getFlavors());
                //↓MP的insert方法只接收单个实体对象
                dishFlavorMapper.insert(flavor);
            }
        }


    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dto) {
        //1.构建分页参数
        Page<Dish> page = new Page<>(dto.getPage(), dto.getPageSize());

        //2.构建查询参数
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(dto.getName()), Dish::getName, dto.getName());
        queryWrapper.eq(dto.getCategoryId() != null, Dish::getCategoryId, dto.getCategoryId());
        queryWrapper.eq(dto.getStatus() != null, Dish::getStatus, dto.getStatus());

        //按执行时间查询
        queryWrapper.orderByAsc(Dish::getUpdateTime);

        Page<Dish> pageResult = dishMapper.selectPage(page, queryWrapper);

        Long total = pageResult.getTotal();
        List<DishDTO> list = pageResult.getRecords()
                .stream()
                .map(dish -> {
            DishDTO dishDTO = new DishDTO();
            BeanUtils.copyProperties(dish, dishDTO);
            //TODO:查询分类名称
            dishDTO.setCategoryName("");
            return dishDTO;
        })
                .collect(Collectors.toList());

        return new PageResult(pageResult.getTotal(), list);
    }

    @Override
    public DishDTO getByIdWithFlavor(Long id) {
        Dish dish = dishMapper.selectById(id);
        if (dish == null)
            return null;

        //转化为DTO
        DishDTO dto = new DishDTO();
        BeanUtils.copyProperties(dish, dto);

        //查询菜品口味
        dto.setFlavors(dishFlavorMapper.getByDishId(dto.getId()));

        return dto;
    }

    @Override
    @CacheEvict(value = "dish", allEntries = true)
    public void updateWithFlavor(DishDTO dto) {
        Dish dish = new Dish();
        BeanUtils.copyProperties(dto, dish);
        dishMapper.updateById(dish);

        dishFlavorMapper.deleteByDishId(dish.getId());

        List<DishFlavor> flavors = dto.getFlavors();
        if (flavors != null && !flavors.isEmpty()) {
            flavors.forEach(flavor -> {
                flavor.setDishId(dto.getId());
                dishFlavorMapper.insert(flavor);
            });
        }
    }

    @Override
    @CacheEvict(value = "dish", allEntries = true)
    public void deleteBatch(List<Long> ids) {
        //1.删除菜品
        dishMapper.deleteByIds(ids);

        //2.删除菜品关联的口味
        for (Long id : ids) {
            dishFlavorMapper.deleteByDishId(id);
        }
    }

    @Override
    @CacheEvict(value = "dish", allEntries = true)
    public void updateStatus(Long id, Integer status) {
        Dish dish = Dish.builder()
                .id(id)
                .status(status)
                .build();

        dishMapper.updateById(dish);
    }

    @Override
    @Cacheable(value = "dish", key = "#categoryId")
    public List<DishDTO> listByCategoryId(Long categoryId) {
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null, Dish::getCategoryId, categoryId);
        //只管起售的菜品
        queryWrapper.eq(Dish::getStatus, 1);

        List<Dish> dishList = dishMapper.selectList(queryWrapper);

        return dishList
                .stream()
                .map(dish -> {
                    DishDTO dto = new DishDTO();
                    BeanUtils.copyProperties(dish, dto);
                    return dto;
                }).collect(Collectors.toList());

    }
}
