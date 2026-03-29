package com.sky.takeout.service;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.takeout.dto.CategoryPageQueryDTO;
import com.sky.takeout.entity.Category;
import com.sky.takeout.result.PageResult;

import java.util.List;

public interface CategoryService {
    //新增分类
    void save(Category category);

    //分类分页查询
    PageResult pageQuery(CategoryPageQueryDTO dto);

    //根据id删除分类
    void deleteById(Long id);

    //修改分类
    void update(Category category);

    //根据类型查询分类
    List<Category> list(Integer type);

    //启用/禁用分类
    void updateStatus(Long id, Integer status);
}
