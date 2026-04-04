package com.sky.takeout.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DishOverViewVO implements Serializable {
    //已起售数量
    private Integer sold;

    //已停售数量
    private Integer discontinued;
}
