package com.sky.takeout.service;

import com.sky.takeout.dto.WxLoginDTO;
import com.sky.takeout.vo.UserLoginVO;

public interface UserService {
    /**
     * 微信登录
     * 1. 用code换取openid
     * 2. 没有账号自动注册
     * 3. 返回token
     */
    UserLoginVO wxLogin(WxLoginDTO wxLoginDTO);
}
