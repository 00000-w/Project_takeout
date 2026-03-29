package com.sky.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.takeout.dto.SetmealDTO;
import com.sky.takeout.dto.SetmealPageQueryDTO;
import com.sky.takeout.entity.Setmeal;
import com.sky.takeout.entity.SetmealDish;
import com.sky.takeout.mapper.SetmealDishMapper;
import com.sky.takeout.mapper.SetmealMapper;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.service.SetmealService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    @Override
    @Transactional //声明式事务   要么都成功，要么都失败（多表操作必须加事务，避免数据不一致）
    public void saveWithDish(SetmealDTO setmealDTO) {
        //把dto中的套餐基本信息拷贝到Setmeal实体（只拷贝同名类型字段）
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        //默认设置套餐为启动状态
        setmeal.setStatus(1);
        //插入套餐主表
        setmealMapper.insert(setmeal);

        Long setmealId = setmeal.getId();
        //获取套餐菜品列表
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        //非空校验，没有菜品的套餐无意义
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(setmealDish -> setmealDish.setSetmealId(setmealId));
            setmealDishMapper.insertBatch(setmealDishes);
        }

    }

    @Override
    public PageResult pageQuery(SetmealPageQueryDTO dto) {
        //构建MP分页对象（页码、每页条数）
        Page<Setmeal> page = new Page<>(dto.getPage(), dto.getPageSize());

        //构建Lambda条件查询器
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(dto.getName()), Setmeal::getName, dto.getName());
        queryWrapper.eq(dto.getCategoryId() != null, Setmeal::getCategoryId, dto.getCategoryId());
        queryWrapper.eq(dto.getStatus() != null, Setmeal::getStatus, dto.getStatus());

        //排序：按更新时间排序
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        //执行分页查询
        Page<Setmeal> pageResult = setmealMapper.selectPage(page, queryWrapper);

        //把Setmeal实体列表转化为DTO列表（供前端展示）
        List<SetmealDTO> list = pageResult.getRecords().stream()
                .map(s -> {
                    SetmealDTO sdto = new SetmealDTO();
                    BeanUtils.copyProperties(s, sdto);
                    sdto.setCategoryName(""); //此处占位ie，实际应查询名称
                    return sdto;
                }).collect(Collectors.toList());
        return new PageResult(pageResult.getTotal(), list);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //只删除起售数据
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId, ids);
        queryWrapper.eq(Setmeal::getStatus, 1);
        Long count = setmealMapper.selectCount(queryWrapper);
        if (count > 0) throw new RuntimeException("有套餐正在起售，不能删除");

        //删除主表数据
        setmealMapper.deleteByIds(ids);

        //删除中间表数据
        for (Long id : ids) {
            setmealDishMapper.deleteBySetmealId(id);
        }
    }

    @Override
    public SetmealDTO getByIdWithDish(Long id) {
        Setmeal setmeal = setmealMapper.selectById(id);
        if (setmeal == null) return null;
        SetmealDTO dto = new SetmealDTO();
        BeanUtils.copyProperties(setmeal, dto);

        List<SetmealDish> bySetmealId = setmealDishMapper.getBySetmealId(id);
        dto.setSetmealDishes(bySetmealId);
        return dto;
    }

    @Override
    @Transactional
    public void updateWithDish(SetmealDTO dto) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(dto, setmeal);
        setmealMapper.updateById(setmeal);

        //删除旧关联
        setmealDishMapper.deleteBySetmealId(dto.getId());

        //创建新关联
        List<SetmealDish> setmealDishes = dto.getSetmealDishes();
        //避免空套餐
        if (setmealDishes != null && !setmealDishes.isEmpty()) {
            setmealDishes.forEach(s -> s.setSetmealId(dto.getId()));
            setmealDishMapper.insertBatch(setmealDishes);
        }
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        Setmeal setmeal = Setmeal.builder().id(id).status(status).build();
        setmealMapper.updateById(setmeal);
    }

    @Override
    public List<SetmealDTO> listByCategoryId(Long categoryId) {
        //查找list
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(categoryId != null, Setmeal::getCategoryId, categoryId);
        queryWrapper.eq(Setmeal::getStatus, 1);
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        return setmealMapper.selectList(queryWrapper).stream()
                .map(s -> {
                    SetmealDTO sdto = new SetmealDTO();
                    BeanUtils.copyProperties(s, sdto);
                    return sdto;
                }).collect(Collectors.toList());

    }
}
