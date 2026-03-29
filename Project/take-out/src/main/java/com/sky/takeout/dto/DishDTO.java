package com.sky.takeout.dto;

import com.sky.takeout.entity.DishFlavor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class DishDTO {
    //只保留需要传输的，屏蔽前端不需要的字段
    private Long id;
    private String name;
    private Long categoryId;
    private BigDecimal price;
    private String image;
    private String description;
    private Integer status;

    //菜品口味数据（一个菜对应多个口味）
    private List<DishFlavor> flavors = new ArrayList<>();

    //分类名称（用于页面显示）
    private String categoryName;
}
