package com.sky.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.dto.EmployeePageQueryDTO;
import com.sky.takeout.entity.Employee;
import com.sky.takeout.mapper.EmployeeMapper;
import com.sky.takeout.service.EmployeeService;
import com.sky.takeout.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/*  员工分页查询第5步：修改Service实现，简化代码
    1.修改save()代码，利用自动填充

    下一步：实现分页查询接口
* */

/*  员工分页查询第10步：实现分页查询Service
    1.构建分页参数
    2.构建查询条件（姓名模糊查询、时间范围查询）
    3.执行分页查询
    4.封装返回结果
    5.处理敏感信息

    下一步：在Controller添加分页接口
* */

/*  员工启动/禁用功能第2步：实现
* */
@Service
public class EmployeeServiceImpl implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    @Override
    public Employee login(String username, String password) {
        Employee employee = employeeMapper.getByUsername(username);
        if (employee == null)
            throw new RuntimeException("用户不存在");

        //对前端传来的密码md5加密
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8));

        //比较密码
        if (!md5Password.equals(employee.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        //检查员工状态
        if (employee.getStatus() == 0) {
            throw new RuntimeException("账号已禁用");
        }

        //登录成功，返回员工信息
        return employee;
    }

    @Override
    public void save(Employee employee) {
        //初始密码
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes(StandardCharsets.UTF_8)));

        //设置状态为启用
        employee.setStatus(1);

       /* 有了自动填充，以下注掉
       //设置创建时间和修改时间为当前时间
        employee.setCreateTime(LocalDateTime.now());
        employee.setUpdateTime(LocalDateTime.now());

        //设置创建人和修改人（应从当前登录用户中获取，暂写死为1L)
        employee.setCreateUser(1L);
        employee.setUpdateUser(1L);*/

        //插入数据库
        //使用MP提供的insert方法
        employeeMapper.insert(employee);
    }

    @Override
    public PageResult pageQuery(EmployeePageQueryDTO dto) {
        //1.构建分页参数
        Page<Employee> page = new Page<>(dto.getPage(), dto.getPageSize());

        //2.构建查询条件
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //姓名模糊查询
        queryWrapper.like(StringUtils.hasText(dto.getName()), Employee::getName, dto.getName());

        //时间范围查询
        //注意：DTO里的时间是LocalDate类型，和Employee的LocalDateTime不一致，不能直接对比
        if (dto.getBeginDate() != null && dto.getEndDate() != null) {
            queryWrapper.between(Employee::getCreateTime,
                    dto.getBeginDate().atStartOfDay(),
                    dto.getEndDate().atTime(LocalTime.MAX));
        }

        //3.执行分页查询
        Page<Employee> pageResult = employeeMapper.selectPage(page, queryWrapper);

        //4.封装返回结果
        Long total = pageResult.getTotal();
        List<Employee> records = pageResult.getRecords();

        //5.处理敏感信息（密码不返回）
        records.forEach(e -> e.setPassword("******"));

        //封装成通用格式
        return new PageResult(total, records);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        //检查员工是否存在
        Employee employee = employeeMapper.selectById(id);
        if (employee == null)
            throw new RuntimeException("员工不存在");

        //当前登录id
        Long currentUserId = UserContext.getCurrentId();

        //3.不能禁用自己
        if (id.equals(currentUserId) && status == 0)
            throw new RuntimeException("不能禁用自己");

        //4.构建更新对象
        Employee updateEmployee = Employee.builder()
                .id(id)
                .status(status)
                .updateTime(LocalDateTime.now())
                .updateUser(currentUserId)
                .build();

        //5.更新
        employeeMapper.updateById(updateEmployee);
    }

    @Override
    public Employee getById(Long id) {
        Employee employee = employeeMapper.selectById(id);

        //没有对应的id员工
        if (employee == null)
            throw new RuntimeException("查无此员工");

        //隐藏密码
        employee.setPassword("******");

        return employee;
    }

    @Override
    public void update(Employee employee) {
        //1.员工是否存在
        Long id = employee.getId();
        Employee dbEmployee = employeeMapper.selectById(id);
        if (dbEmployee == null)
            throw new RuntimeException("用户不存在");

        //2.设置更新时间和更新用户
        employee.setUpdateTime(LocalDateTime.now());

        employee.setUpdateUser(UserContext.getCurrentId());

        //3.执行更新
        employeeMapper.updateById(employee);
    }


}
