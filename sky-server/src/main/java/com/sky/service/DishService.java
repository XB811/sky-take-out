package com.sky.service;

import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.vo.DishVO;

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

    /**
    * 根据id查询菜品
    * @param id
    * @return DishVO
    * @Date 2024/9/11 22:17
    */
    DishVO getById(Long id);

    /**
    * 修改菜品
    * @param dishDTO
    * @return
    * @Date 2024/9/11 22:17
    */
    void updateWithFlavor(DishDTO dishDTO);

    /**
    * 菜品起售、停售
    * @param status 
     * @param id 
    * @return 
    * @Date 2024/9/12 01:19
    */
    void startOrStop(Integer status,Long id);

    /**
    * 根据分类id查询菜品
    * @param categoryId 
    * @return List<DishVO> 
    * @Date 2024/9/12 01:19
    */
    List<DishVO> getByCategoryId(Integer categoryId);
}

