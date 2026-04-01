package com.sky.takeout.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@TableName("address_book")
public class AddressBook {
    @TableId(type= IdType.AUTO)
    private Long id;

    //所属用户id
    private Long userId;

    //收货人姓名
    private String consignee;

    //手机号
    private String phone;

    //性别 0女1男
    private String sex;

    //省级区划编号
    private String provinceCode;
    //省级名称
    private String provinceName;

    //市级区划编号
    private String cityCode;
    //市级名称
    private String cityName;

    //详细地址
    private String detail;

    //标签 （家、公司、学校）
    private String label;

    //是否默认地址 0否1是
    @TableField("is_default")
    private Integer isDefault;
}
