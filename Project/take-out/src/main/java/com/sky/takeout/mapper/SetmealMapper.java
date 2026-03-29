package com.sky.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sky.takeout.entity.Category;
import com.sky.takeout.entity.Setmeal;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SetmealMapper extends BaseMapper<Setmeal> {
    //根据分类id得到套餐数量
    @Select("SELECT COUNT(*) FROM setmeal WHERE category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);
}
