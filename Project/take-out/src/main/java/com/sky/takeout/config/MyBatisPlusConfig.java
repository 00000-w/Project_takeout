package com.sky.takeout.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/* 三.员工分页查询第1步——我做了哪些事？
   1.pom.xml引入mp依赖，注掉原本的mybatis依赖
   2.修改yaml文件（逻辑删除）
   3.新建config包
   4.包下写配置类
   5.类中告诉MP用的数据库是哪一个并返回插件容器（Bean的作用：把工厂造的分页机器搬到大厅让所有人都能用）

   下一步？ -> 修改Employee实体类，添加MyBatis-Plus注解
* */

//配置类
@Configuration
public class MyBatisPlusConfig { //见面知意-MP的配置类
    @Bean //创建“全局可用的工具对象”
    //告诉MP我用的是mysql数据库
    public MybatisPlusInterceptor myBatisPlusInterceptor() {
        //创建一个插件容器
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        //new...MYBATIS -> 创建MySQL版本的分页插件
        //interceptor.addInnerInterceptor -> 把分页插件放入插件容器
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        //返回插件容器，Spring会把它放容器里，全局生效
        return interceptor;
    }
}

