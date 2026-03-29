package com.sky.takeout.dto;

import lombok.Data;

@Data
public class SetmealPageQueryDTO {
    private Integer page;
    private Integer pageSize;

    private String name;
    private Long categoryId;
    private Integer status;
}
