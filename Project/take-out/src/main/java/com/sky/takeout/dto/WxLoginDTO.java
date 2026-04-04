package com.sky.takeout.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class WxLoginDTO implements Serializable {
    /**
     * 微信小程序调用 wx.login() 获取的临时登录凭证
     * 有效期5分钟，只能用一次
     */
    private String code;
}
