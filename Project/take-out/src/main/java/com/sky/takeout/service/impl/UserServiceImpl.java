package com.sky.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sky.takeout.config.WeChatProperties;
import com.sky.takeout.dto.WxLoginDTO;
import com.sky.takeout.entity.User;
import com.sky.takeout.mapper.UserMapper;
import com.sky.takeout.service.UserService;
import com.sky.takeout.utils.JwtUtil;
import com.sky.takeout.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WeChatProperties weChatProperties;

    @Autowired
    private RestTemplate restTemplate;

    // 微信登录API地址（{} 是RestTemplate的占位符）
    private static final String WX_LOGIN_URL =
            "https://api.weixin.qq.com/sns/jscode2session" +
                    "?appid={appid}&secret={secret}&js_code={code}&grant_type=authorization_code";

    @Override
    public UserLoginVO wxLogin(WxLoginDTO wxLoginDTO) {

        // =========== 第一步：用code换取openid ===========
        String openid = getOpenid(wxLoginDTO.getCode());
        log.info("微信登录获取到openid：{}", openid);

        // =========== 第二步：查询用户，不存在则注册 ===========
        User user = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getOpenid, openid)
        );

        if (user == null) {
            // 自动注册新用户
            user = User.builder()
                    .openid(openid)
                    .createTime(LocalDateTime.now())
                    .build();
            userMapper.insert(user);
            log.info("新用户注册成功，userId：{}", user.getId());
        }

        // =========== 第三步：生成JWT ===========
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getId());
        // 加role区分用户token和员工token
        claims.put("role", "user");
        String token = JwtUtil.generateToken(claims);

        // =========== 第四步：返回VO ===========
        return UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
    }

    /**
     * 调用微信API，用code换取openid
     */
    private String getOpenid(String code) {
        // 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("appid", weChatProperties.getAppid());
        params.put("secret", weChatProperties.getSecret());
        params.put("code", code);

        // 发送HTTP GET请求
        String result = restTemplate.getForObject(WX_LOGIN_URL, String.class, params);
        log.info("微信API响应：{}", result);

        // 解析JSON响应
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(result);

            // 检查是否有错误码
            if (jsonNode.has("errcode")) {
                int errcode = jsonNode.get("errcode").asInt();
                if (errcode != 0) {
                    String errmsg = jsonNode.get("errmsg").asText();
                    log.error("微信登录失败，errcode：{}，errmsg：{}", errcode, errmsg);
                    throw new RuntimeException("微信登录失败：" + errmsg);
                }
            }

            // 返回openid
            return jsonNode.get("openid").asText();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("解析微信响应失败：", e);
            throw new RuntimeException("微信登录响应解析失败");
        }
    }
}
