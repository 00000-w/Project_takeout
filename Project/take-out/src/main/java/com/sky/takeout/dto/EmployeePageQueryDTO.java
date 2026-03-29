package com.sky.takeout.dto;

/*  员工分页查询第6步：创建分页查询的DTO（数据传输对象）
    1.页码、每页记录数
    2.查询条件

    下一步：创建返回的VO
* */

import lombok.Data;

import java.time.LocalDate;

@Data
public class EmployeePageQueryDTO {
    //页码， 默认值1：前端不传参时，默认查第一页
    private Integer page = 1;
    //每页记录数， 默认值10：前端不传参时，默认每页10条
    private Integer pageSize = 10;

    //查询条件
    private String name;
    private LocalDate beginDate;
    private LocalDate endDate;
}
