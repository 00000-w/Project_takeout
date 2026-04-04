package com.sky.takeout.utils;

/* 用户上下文工具类
    利用ThreadLocal，在同一个请求线程中共享当前登录用户的ID
    每个请求线程独立，互不干扰
* */

public class UserContext {
    //ThreadLocal：每个线程都有自己独立的副本，线程安全
    private static final ThreadLocal<Long> USER_ID = new ThreadLocal<>();

    //存入当前用户ID
    public static void setCurrentId(Long id) {
        USER_ID.set(id);
    }

    //获取当前用户ID
    public static Long getCurrentId() {
        return USER_ID.get();
    }

    //清除 （必须在请求结束时调用，防止内存泄露）
    public static void clear() {
        USER_ID.remove();
    }
}