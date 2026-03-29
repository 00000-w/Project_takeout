package com.sky.takeout.controller;

import com.sky.takeout.dto.CategoryPageQueryDTO;
import com.sky.takeout.entity.Category;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.result.Result;
import com.sky.takeout.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.PublicKey;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    //新增分类
    @PostMapping
    public Result<Void> save(@RequestBody Category category) {
        categoryService.save(category);

        return Result.success();
    }

    //分类分页查询
    @GetMapping("/page")
    public Result<PageResult> page(CategoryPageQueryDTO dto) {
        PageResult pageResult = categoryService.pageQuery(dto);

        return Result.success(pageResult);
    }

    //根据id删除分类
    @DeleteMapping
    public Result<Void> list(Long id) {
        categoryService.deleteById(id);

        return Result.success();
    }

    //修改分类
    @PutMapping
    public Result<Void> update(@RequestBody Category category) {
        categoryService.update(category);

        return Result.success();
    }

    //根据类型查询分类
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type) {
        List<Category> list = categoryService.list(type);

        return Result.success(list);
    }

    //启用/禁用分类
    @PostMapping("status/{status}")
    public Result<Void> updateStatus(@PathVariable Integer status, @RequestBody Map<String, Long> request) {
        categoryService.updateStatus(request.get("id"), status);

        return Result.success();
    }
}
