package com.sky.takeout.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginVO {
    //jwt令牌（前端后续每次请求都要带上）
    private String token;

    //员工基本信息
    private Long id;
    private String username;
    private String name;
}
