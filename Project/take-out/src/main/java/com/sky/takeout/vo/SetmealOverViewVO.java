package com.sky.takeout.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SetmealOverViewVO implements Serializable {
    //已起售数量
    private Integer sold;

    //已停售数量
    private Integer discontinued;
}
