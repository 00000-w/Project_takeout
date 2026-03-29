package com.sky.takeout.controller;

import com.sky.takeout.dto.DishDTO;
import com.sky.takeout.dto.DishPageQueryDTO;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.result.Result;
import com.sky.takeout.service.DishService;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.One;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("admin/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    //新增菜品
    @PostMapping
    public Result<Void> save(@RequestBody  DishDTO dto) {
        dishService.saveWithFlavor(dto);

        return Result.success();
    }

    //菜品分页查询
    @GetMapping("/page")
    public Result<PageResult> page(DishPageQueryDTO dto) {
        PageResult pageResult = dishService.pageQuery(dto);

        return Result.success(pageResult);
    }

    //根据id查询菜品
    @GetMapping("/{id}")
    public Result<DishDTO> getById(@PathVariable Long id) {
        DishDTO dto = dishService.getByIdWithFlavor(id);

        return Result.success(dto);
    }

    //修改菜品
    @PutMapping
    public Result<Void> update(@RequestBody DishDTO dto) {
        dishService.updateWithFlavor(dto);

        return Result.success();
    }

    //批量删除菜品
    @DeleteMapping
    public Result<Void> deleteBatch(@RequestParam String ids) {
        List<Long> idLong = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());

        dishService.deleteBatch(idLong);

        return Result.success();
    }

    //菜品起售/停售
    @PostMapping("status/{status}")
    public Result<Void> updateStatus(@RequestParam Integer status, Long id) {
        dishService.updateStatus(id, status);

        Result<Void> result = new Result<>();
        result.setMsg(status == 1 ? "起售成功" : "停售成功");
        return result;
    }


    //根据分类id查询菜品
    @GetMapping("/list")
    public Result<List<DishDTO>> listByCategoryId(@RequestParam Long id) {
        List<DishDTO> list = dishService.listByCategoryId(id);

        return Result.success(list);
    }
}
