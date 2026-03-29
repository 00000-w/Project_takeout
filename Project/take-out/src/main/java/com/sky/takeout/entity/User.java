package com.sky.takeout.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data

@TableName("user")
public class User {
    private Long id;
    private String name;
}
