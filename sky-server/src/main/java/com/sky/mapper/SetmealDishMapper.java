package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
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

    /**
    * 批量插入菜品数据
    * @param dishes
    * @return
    * @Date 2024/9/12 01:35
    */
    void insertBatch(List<SetmealDish> dishes);
}
