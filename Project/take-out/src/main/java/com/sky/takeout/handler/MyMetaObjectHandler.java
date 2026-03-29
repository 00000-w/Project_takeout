package com.sky.takeout.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


/* 员工分页查询第3步——创建自动填充处理器
 *   1.创建handler包 （这个包专门放“数据填充/处理”相关的代码）
 *   2.创建这个类 对应MP框架提供的“核心接口”
 *   3.重写需要的方法
 *
 *  下一步：创建MyBatis-Plus的Mapper基接口：修改 EmployeeMapper.java，让它继承MyBatis-Plus的 BaseMapper
 * */

//这个注解是核心！！！告诉Spring这是一个组件，启动时要加载到容器里去
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    //插入时自动填充
    @Override
    public void insertFill(MetaObject metaObject) {
        //strictInsertFill（安全填充方法）详解
        // 1.metaObject：MP封装的实体对象（比如Employee），不用直接操作实体类
        // 2."createTime"：实体类的属性名
        // 3. LocalDateTime.class：属性的类型
        // 4. LocalDateTime.now()：要填充的具体值
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
        this.strictInsertFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        //TODO:这里暂时写死1L，后续从登录用户获取真实ID
        this.strictInsertFill(metaObject, "createUser", Long.class, 1L);
        this.strictInsertFill(metaObject, "updateUser", Long.class, 1L);
    }

    @Override
    //更新时自动填充
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        //TODO:应从登录用户获取
        this.strictUpdateFill(metaObject, "updateUser", Long.class, 1L);
    }
}
