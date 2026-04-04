package com.sky.takeout.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sky.wechat")
public class WeChatProperties {
    private String appid;
    private String secret;
}
