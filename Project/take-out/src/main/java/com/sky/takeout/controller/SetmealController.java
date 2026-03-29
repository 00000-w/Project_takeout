package com.sky.takeout.controller;

import com.sky.takeout.dto.SetmealDTO;
import com.sky.takeout.dto.SetmealPageQueryDTO;
import com.sky.takeout.mapper.SetmealMapper;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.result.Result;
import com.sky.takeout.service.SetmealService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    @PostMapping
    public Result<Void> save(@RequestBody SetmealDTO setmealDTO) {
        setmealService.saveWithDish(setmealDTO);

        return Result.success();
    }

    @GetMapping("/page")
    public Map<String, Object> page (SetmealPageQueryDTO setmealPageQueryDTO) {
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "分页查询成功");
        result.put("data", pageResult);
        return result;
    }

    @DeleteMapping
    //admin/setmeal?ids=1,2,3
    public Result<Void> delete(@RequestParam String ids) {
        List<Long> idList = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        setmealService.deleteBatch(idList);

        return Result.success();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        SetmealDTO byIdWithDish = setmealService.getByIdWithDish(id);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 1);
        result.put("msg", "查询成功");
        result.put("data", byIdWithDish);
        return result;
    }

    @PutMapping
    public Result<Void> update(@RequestBody SetmealDTO setmealDTO) {
        setmealService.updateWithDish(setmealDTO);

        return Result.success();
    }

    @PostMapping("status/{status}")
    public Result<Void> updateStatus(@PathVariable Integer status, Long id) {
        setmealService.updateStatus(status, id);

        Result<Void> result = new Result<>();
        result.setMsg(status == 1 ? "起售成功" : "停售成功");
        return Result.success();
    }

    @GetMapping("/list")
    public Result<List<SetmealDTO>> listByCategoryId(Long categoryId) {
        List<SetmealDTO> list = setmealService.listByCategoryId(categoryId);

        return Result.success(list);
    }
}
