package com.sky.mapper;

import com.sky.entity.Dish;
import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

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

    /**
    * 根据套餐ids批量删除对应的菜品
    * @param setmealIds
    * @return
    * @Date 2024/9/12 11:43
    */
    void deleteBySetmealIdBatch(List<Long> setmealIds);

    /**
    * 根据套餐id查询对应的菜品
    * @param setmealId
    * @return List<SetmealDish>
    * @Date 2024/9/12 11:53
    */
    @Select("select * from setmeal_dish where setmeal_id= #{setmealId}")
    List<SetmealDish> getByDishId(Long setmealId);

    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);
}
