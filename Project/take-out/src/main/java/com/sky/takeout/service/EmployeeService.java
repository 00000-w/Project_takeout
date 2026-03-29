package com.sky.takeout.service;

import com.sky.takeout.result.PageResult;
import com.sky.takeout.dto.EmployeePageQueryDTO;
import com.sky.takeout.entity.Employee;

/*  员工分页查询第8步：在Service接口添加分页方法
    1.增加方法

    下一步：创建分页结果封装类
* */

/*  实现员工启用/禁用功能第1步:添加方法
*
* */

public interface EmployeeService {
    //员工登录
    Employee login(String username, String password);

    //添加员工
    void save(Employee employee);

    //分页方法
    PageResult pageQuery(EmployeePageQueryDTO dto);

    //启动/禁用员工账号
    void updateStatus(Long id, Integer status);

    //根据id查询员工
    Employee getById(Long id);

    //更新员工信息
    void update(Employee employee);
}
