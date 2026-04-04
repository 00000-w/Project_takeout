package com.sky.takeout.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private JwtInterceptor jwtInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtInterceptor) //注册拦截器
                //拦截所有请求
                .addPathPatterns("/**")
                //放行登录接口
                .excludePathPatterns("/admin/employee/login") //de登录接口不检验token
                //放行图片访问（不登陆就能看）
                .excludePathPatterns("/uploads/**")
                .excludePathPatterns("/user/login")// 用户微信登录
                .excludePathPatterns("/ws/**");
    }

    //让 /uploads/** 能访问到磁盘上的图片
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");
    }
}
