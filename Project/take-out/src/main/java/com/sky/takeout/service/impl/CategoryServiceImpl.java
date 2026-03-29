package com.sky.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.takeout.dto.CategoryPageQueryDTO;
import com.sky.takeout.entity.Category;
import com.sky.takeout.mapper.CategoryMapper;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public void save(Category category) {
        //设置状态为启用
        category.setStatus(1);
        //排序默认为0
        if (category.getSort() == null)
            category.setSort(0);

        categoryMapper.insert(category);
    }

    @Override
    public PageResult pageQuery(CategoryPageQueryDTO dto) {
        //构建分页参数
        Page<Category> page = new Page<>(dto.getPage(), dto.getPageSize());

        //构建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        queryWrapper.like(StringUtils.hasText(dto.getName()), Category::getName, dto.getName());
        queryWrapper.eq(dto.getType() != null, Category::getType, dto.getType());

        //排序：按sort字段升序
        queryWrapper.orderByAsc(Category::getSort);

        //执行分页查询
        Page<Category> PageResult = categoryMapper.selectPage(page, queryWrapper);

        //封装自定义分页结果
        Long total = PageResult.getTotal();
        List<Category> records = PageResult.getRecords();

        return new PageResult(total, records);
    }

    @Override
    public void deleteById(Long id) {
        //TODO：实际要检查下面有没有套餐、菜品，这里先简单删除
        if (categoryMapper.selectById(id) == null)
            throw new RuntimeException("该分类不存在");

        categoryMapper.deleteById(id);
    }

    @Override
    public void update(Category category) {
        if (categoryMapper.selectById(category.getId()) == null)
            throw new RuntimeException("该分类不存在");
        categoryMapper.updateById(category);
    }

    @Override
    public List<Category> list(Integer type) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(type != null, Category::getType, type);
        //只查询启用状态的分类
        queryWrapper.eq(Category::getStatus, 1);
        //按sort字段升序
        queryWrapper.orderByAsc(Category::getSort);

        return categoryMapper.selectList(queryWrapper);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        Category category = categoryMapper.selectById(id);
        if (category == null)
            throw new RuntimeException("不存在此分类");

        if (status == null || (status != 0 && status != 1))
            throw new RuntimeException("状态不合法");

        Category updateCategory = Category.builder()
                .id(id)
                .status(status)
                .build();

        categoryMapper.updateById(updateCategory);
    }
}
