package com.sky.takeout.vo;

/*  员工分页查询第7步：创建返回的VO（视图对象）
    1.写属性

    下一步： 在Service接口添加分页方法
* */

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmployeeVO {
    private Long id;
    private String username;
    private String name;
    private String phone;
    private String sex;
    private String inNumber;
    private Integer status;
    private LocalDateTime createTime;
}
