package com.sky.takeout.controller;

import com.sky.takeout.dto.ShoppingCartDTO;
import com.sky.takeout.entity.ShoppingCart;
import com.sky.takeout.result.Result;
import com.sky.takeout.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController //= @Controller + @ResponseBody -> 不用在每个方法上写@ResponseBody
@RequestMapping("user/shoppingCart")   /*请求路径的统一前缀  /user/ 表示用户端，区分模块*/
public class ShoppingCartController {
    @Autowired  //依赖注入，controller层不直接操作数据库，通过Service层处理业务逻辑
    private ShoppingCartService shoppingCartService;

    //添加购物车 -> POST
    @PostMapping("/add")
    public Result<ShoppingCart> add(@RequestBody ShoppingCartDTO shoppingCartDTO) {
        ShoppingCart shoppingCart = shoppingCartService.add(shoppingCartDTO);

        return Result.success(shoppingCart);
    }

    //查看购物车 -> GET
    @GetMapping("/list")
    public Result<List<ShoppingCart>> list() {
        List<ShoppingCart> list = shoppingCartService.list();

        return Result.success(list);
    }

    //清口购物车 -> DELETE
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        shoppingCartService.clean();

        return Result.success();
    }
}
