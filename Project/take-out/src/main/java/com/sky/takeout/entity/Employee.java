package com.sky.takeout.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/*  员工分页查询第2步-给Employee类加MP注解
    1.指定表名
    2.主键配置
    3.对必要的字段（易产生歧义的）进行显式标注（其实驼峰映射已经能自动转换，这里是为了让代码更清晰更了然）
    4.自动填充

    下一步？ -> 创建自动填充处理器
* */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//指定表名
@TableName("employee")
public class Employee {
    //主键自增
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String name;
    private String password;
    private String phone;
    private String sex;
    //字段名手动映射
    @TableField("id_number")
    private String idNumber;
    private Integer status;
    //只有插入时赋值
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    //插入/更新时都赋值
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    @TableField(fill = FieldFill.INSERT)
    private Long createUser;
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUser;
}
