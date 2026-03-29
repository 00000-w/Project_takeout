package com.sky.takeout.dto;

import com.sky.takeout.entity.SetmealDish;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class SetmealDTO {
    private Long id;
    private Long categoryId;
    private String name;
    private BigDecimal price;
    private Integer status;
    private String description;
    private String image;
    //为什么初始化？
    /*
    * 避免空指针异常！如果不初始化，前端传参时若没传菜品列表，
    * 后端处理setmealDishes.add()会报错；初始化后即使没有菜品，也是空列表而非 null，代码更健壮。*/
    private List<SetmealDish> setmealDishes = new ArrayList<>();
    private String categoryName;
}
