package com.sky.takeout.controller;

import com.sky.takeout.dto.WxLoginDTO;
import com.sky.takeout.result.Result;
import com.sky.takeout.service.UserService;
import com.sky.takeout.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 微信登录
     * 小程序调用 wx.login() 拿到code，传给这个接口
     */
    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody WxLoginDTO wxLoginDTO) {
        log.info("微信用户登录，code：{}", wxLoginDTO.getCode());
        UserLoginVO loginVO = userService.wxLogin(wxLoginDTO);
        return Result.success(loginVO);
    }
}
