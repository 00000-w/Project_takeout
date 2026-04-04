package com.sky.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.sky.takeout.entity.AddressBook;
import com.sky.takeout.mapper.AddressBookMapper;
import com.sky.takeout.service.AddressBookService;
import com.sky.takeout.utils.UserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AddressBookServiceImpl implements AddressBookService {
    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public void add(AddressBook addressBook) {
        addressBook.setUserId(UserContext.getCurrentId());

        //如果是第一条地址，自动设为默认
        Long count = addressBookMapper.selectCount(new LambdaQueryWrapper<AddressBook>().eq(AddressBook::getUserId, UserContext.getCurrentId()));
        addressBook.setIsDefault(count == 0 ? 1 : 0);
        addressBookMapper.insert(addressBook);
    }

    @Override
    public List<AddressBook> list() {
        return addressBookMapper.selectList(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getUserId, UserContext.getCurrentId())
                //默认地址排最前
                .orderByDesc(AddressBook::getIsDefault));
    }

    @Override
    public AddressBook getById(Long id) {
        AddressBook addressBook = addressBookMapper.selectById(id);
        if (addressBook == null)
            throw new RuntimeException("地址不存在");
        return addressBook;
    }

    @Override
    public void update(AddressBook addressBook) {
        if (addressBookMapper.selectById(addressBook.getId()) == null)
            throw new RuntimeException("地址不存在");
        addressBookMapper.updateById(addressBook);
    }

    @Override
    public void deleteById(Long id) {
        if (addressBookMapper.selectById(id) == null)
            throw new RuntimeException("地址不存在");
        addressBookMapper.deleteById(id);
    }

    @Override
    public AddressBook getDefault() {
        AddressBook addressBook = addressBookMapper.selectOne(new LambdaQueryWrapper<AddressBook>()
                .eq(AddressBook::getUserId, UserContext.getCurrentId())
                .eq(AddressBook::getIsDefault, 1)
        );
        if (addressBook == null)
            throw new RuntimeException("未设置默认地址");
        return addressBook;
    }

    @Override
    @Transactional //清0 + 设1
    public void setDefault(Long id) {
        AddressBook addressBook = addressBookMapper.selectById(id);
        if (addressBook == null)
            throw new RuntimeException("地址不存在");

        //把所有的地址改为非默认（因为我们不知道哪个是默认地址，不然还要查，白写大段代码。代码越少bug越少，直接全部设0）
        addressBookMapper.update(null, new LambdaUpdateWrapper<AddressBook>()
                .eq(AddressBook::getUserId, UserContext.getCurrentId())
                .set(AddressBook::getIsDefault, 0));
        //根据id设置为默认地址
        addressBookMapper.update(null, new LambdaUpdateWrapper<AddressBook>()
                .eq(AddressBook::getUserId, UserContext.getCurrentId())
                .eq(AddressBook::getId, id)
                .set(AddressBook::getIsDefault, 1));
    }
}
