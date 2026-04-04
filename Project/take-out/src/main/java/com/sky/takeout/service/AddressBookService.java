package com.sky.takeout.service;

import com.sky.takeout.entity.AddressBook;

import java.util.List;

public interface AddressBookService {
    //新增地址
    void add(AddressBook addressBook);

    //查询当前用户所有地址
    List<AddressBook> list();

    //根据id查地址
    AddressBook getById(Long id);

    //修改地址
    void update(AddressBook addressBook);

    //删除地址
    void deleteById(Long id);

    //设置默认地址
    void setDefault(Long id);

    //查询默认地址
    AddressBook getDefault();
}
