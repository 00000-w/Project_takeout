package com.sky.takeout.controller;

import com.sky.takeout.entity.AddressBook;
import com.sky.takeout.result.Result;
import com.sky.takeout.service.AddressBookService;
import lombok.Data;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/addressBook")
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    //新增地址
    @PostMapping
    public Result<Void> add(@RequestBody AddressBook addressBook) {
        addressBookService.add(addressBook);

        return Result.success();
    }

    //查询当前用户所有地址
    @GetMapping
    public Result<List<AddressBook>> list() {
        List<AddressBook> list = addressBookService.list();

        return Result.success(list);
    }

    //查询默认地址
    @GetMapping("/default")
    public Result<AddressBook> getDefault() {
        AddressBook addressBook = addressBookService.getDefault();

        return Result.success(addressBook);
    }

    //根据id查询地址
    @GetMapping("/{id}")
    public Result<AddressBook> getById(@PathVariable Long id) {
        AddressBook addressBook = addressBookService.getById(id);

        return Result.success(addressBook);
    }

    //修改地址
    @PutMapping
    public Result<Void> update(@RequestBody AddressBook addressBook) {
        addressBookService.update(addressBook);

        return Result.success();
    }

    //删除地址
    @DeleteMapping
    public Result<Void> delete(@RequestParam Long id) {
        addressBookService.deleteById(id);

        return Result.success();
    }

    //设置默认地址
    @PutMapping("/default")
    public Result<Void> setDefault(@RequestBody AddressBook addressBook) {
        addressBookService.setDefault(addressBook.getId());

        return Result.success();
    }
}
