package com.sky.mapper;

import com.sky.entity.Dish;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @FileName SetmealDishMapper
 * @Description 操作 套餐---菜品 表
 * @Author xb
 * @date 2024-09-11
 **/
@Mapper
public interface SetmealDishMapper {

    /**
    * 根据菜品id查询套餐
    * @param dishIds 
    * @return List<long> 
    * @Date 2024/9/11 20:15
    */

    List<Long> getSetmealDishByDishIds(List<Long> dishIds);
}
