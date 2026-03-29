package com.sky.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.takeout.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper extends BaseMapper<SetmealDish> {
    //根据套餐Id删除关联菜品
    @Delete("DELETE FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    //根据套餐id查询关联的菜品id列表
    @Select("SELECT dish_id FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    List<Long> getDishIdBySetmealId(Long setmealId);

    //根据套餐id查询套餐菜品关系详情
    @Select("SELECT * FROM setmeal_dish WHERE setmeal_id = #{setmealId}")
    List<SetmealDish> getBySetmealId(Long setmealId);

    //批量插入套餐菜品关系（批量插入 动态SQL -> XML实现  需要@Param)
    int insertBatch(@Param("list") List<SetmealDish> setmealDishes);

}
