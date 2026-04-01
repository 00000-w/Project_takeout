package com.sky.takeout.controller;

import com.sky.takeout.dto.EmployeePageQueryDTO;
import com.sky.takeout.entity.Employee;
import com.sky.takeout.result.PageResult;
import com.sky.takeout.result.Result;
import com.sky.takeout.service.EmployeeService;
import com.sky.takeout.utils.JwtUtil;
import javax.servlet.http.HttpServletRequest;

import com.sky.takeout.vo.LoginVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/*  员工分页查询第11步：在Controller添加分页接口
* */

/*  员工启用/禁用第3步：添加接口
* */
@RestController
@RequestMapping("admin/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody Map<String, String> loginForm) {
        //前端传参调取用户名和密码
        String username = loginForm.get("username");
        String password = loginForm.get("password");

        Employee employee = employeeService.login(username, password);

        //生成jwt令牌
        //只存核心信息（id,username)，不存敏感信息（密码、身份证）
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", employee.getId());
        claims.put("username", employee.getUsername());
        String token = JwtUtil.generateToken(claims);

        LoginVO loginVO = LoginVO.builder()
                .token(token)
                .id(employee.getId())
                .username(employee.getUsername())
                .name(employee.getName())
                .build();

        return Result.success(loginVO);
    }

    @PostMapping("/logout")
    public  Result<Void> logout(HttpServletRequest request) {
        //jwt令牌一旦生成，在过期前始终有效
        //实际项目中要把token拉入黑名单，此处为学习选择简化

        return Result.success();
    }

    @PostMapping
    public Result<Void> save(@RequestBody Employee employee) {
        employeeService.save(employee);

        return Result.success();
    }

    @GetMapping("/page")
    public Result<PageResult> page(EmployeePageQueryDTO dto) {
        PageResult pageResult = employeeService.pageQuery(dto);

        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    public Result<Void> status(@PathVariable Integer status, @RequestBody Map<String, Long> request) {
        Long id = request.get("id");

        employeeService.updateStatus(id, status);

        Result<Void> result = new Result<>();
        result.setMsg(status == 1 ? "启用成功" : "禁用成功");
        return result;
    }

    //根据id查询员工
    @GetMapping("/{id}")
    public Result<Employee> getById(@PathVariable Long id) {
        Employee employee = employeeService.getById(id);

        return  Result.success(employee);
    }

    //更新员工信息
    //此处为Put
    @PutMapping("update")
    public Result<Void> update(@RequestBody Employee employee) {
        employeeService.update(employee);

        return Result.success();
    }
}
