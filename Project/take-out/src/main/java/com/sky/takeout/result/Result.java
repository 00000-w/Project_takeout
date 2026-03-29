package com.sky.takeout.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private Integer code;
    private String msg;
    private T data;
    private LocalDateTime timestamp;

    //工厂方法：专门负责创建对象的静态方法

    //成功无数据
    public static <T> Result<T> success() {
        return new Result<>(1, "操作成功", null, LocalDateTime.now());
    }

    //成功有数据
    public static <T> Result<T> success(T data) {
        return new Result<>(1, "操作成功", data, LocalDateTime.now());
    }

    //失败
    public static <T> Result<T> error(String msg) {
        return new Result<>(0, msg, null, LocalDateTime.now());
    }
}
