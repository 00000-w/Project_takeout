package com.sky.takeout.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * 应用通用配置
 */
@Configuration
public class AppConfig {
    /**
     * RestTemplate：Spring提供的http请求工具
     * 用于调用微信api
     */
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
