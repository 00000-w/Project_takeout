package com.sky.takeout.result;

/*  员工分页查询第9步：创建分页结果封装类
    1.@Data、@NoArgsConstructor、@AllArgsConstructor`
    2.写总记录数、当前页数据

    下一步：实现分页查询service
* */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult {
    //总记录数
    private Long total;
    //当前页数据
    private List<?> records;
}
