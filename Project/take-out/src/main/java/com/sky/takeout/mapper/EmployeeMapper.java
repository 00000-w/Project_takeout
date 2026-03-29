package com.sky.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.takeout.entity.Employee;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

/*  员工分页查询第4步：创建MP的Mapper基接口
*   1.让EmployeeMapper继承BaseMapper
*   2.把多余代码去除，现在有了几十个内置方法，不用写SQL就能实现增删改查
*
*   下一步：修改Service实现，简化代码
* */

@Mapper
public interface EmployeeMapper extends BaseMapper<Employee> {
    //原本的方法可以保留，但BaseMapper已提供大部分CRUD
    @Select("SELECT * FROM employee WHERE username = #{username}")
    //保留，因为登录需要
    Employee getByUsername(String username);

    /*//添加员工
    @Insert("INSERT INTO employee (username, name, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user) " +
            " VALUES (#{username}, #{name}, #{password}, #{phone}, #{sex}, #{idNumber}, #{status}, #{createTime}, #{updateTime}, #{createUser}, #{updateUser})")
    //插入后获取自增主键
    @Options(useGeneratedKeys = true, keyProperty = "id")
    //单条插入用void（失败抛异常）
    void insertEmployee(Employee employee);*/
}

