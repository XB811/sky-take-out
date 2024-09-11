package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @FileName DishFlavorMapper
 * @Description 插入口味表
 * @Author xb
 * @date 2024-09-11
 **/
@Mapper
public interface DishFlavorMapper {
    
    /**
    * 批量插入口味数据
    * @param flavors 
    * @return 
    * @Date 2024/9/11 17:41
    */
    void insertBatch(List<DishFlavor> flavors);

    /**
    * 根据菜品id删除对应口味
    * @param dishId
    * @return 
    * @Date 2024/9/11 20:27
    */
    @Delete("delete from dish_flavor where dish_id = #{dishId}")
    void deleteByDishId(Long dishId);
}
