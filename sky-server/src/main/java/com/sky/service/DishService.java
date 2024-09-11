package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;

import java.util.List;

/**
 * @FileName DishService
 * @Description
 * @Author xb
 * @date 2024-09-11
 **/

public interface DishService {
    /**
    * 新增菜品，同时添加口味
    * @param dishDTO
    * @return
    * @Date 2024/9/11 17:15
    */
    void saveWithFlavor(DishDTO dishDTO);

    /**
    * 菜品分页查询
    * @param dishPageQueryDTO 
    * @return PageResult 
    * @Date 2024/9/11 19:21
    */
    PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
    * 批量删除菜品
    * @param ids 
    * @return 
    * @Date 2024/9/11 19:21
    */
    void deleteBatch (List<Long> ids);
}
