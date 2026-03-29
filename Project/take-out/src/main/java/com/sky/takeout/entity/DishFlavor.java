package com.sky.takeout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

@TableName("dish_flavor")
public class DishFlavor {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long dishId;
    private String name;
    private String value;
}
