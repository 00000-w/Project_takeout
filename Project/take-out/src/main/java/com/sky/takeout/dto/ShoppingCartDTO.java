package com.sky.takeout.dto;

import lombok.Data;

import java.io.Serializable;

@Data
//Serializable是一个标记接口，没有任何方法，用来表示这个类的对象可以被序列化
public class ShoppingCartDTO implements Serializable {
    private Long dishId;
    private Long setmealId;
    private String dishFlavor;
}
