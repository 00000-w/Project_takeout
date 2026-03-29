package com.sky.takeout.dto;

import lombok.Data;

@Data
public class CategoryPageQueryDTO {
    //分页参数
    private Integer page = 1;
    private Integer pageSize = 10;

    //查询条件
    private String name;
    private Integer type;
}
