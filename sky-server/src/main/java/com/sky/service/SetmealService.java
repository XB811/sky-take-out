package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;

/**
 * @FileName SetmealService
 * @Description
 * @Author xb
 * @date 2024-09-12
 **/

public interface SetmealService {
    /**
    * 新增套餐
    * @param setmealDTO
    * @return
    * @Date 2024/9/12 01:00
    */
    void saveWithDish(SetmealDTO setmealDTO);

    /**
    * 套餐分页查询
    * @param setmealPageQueryDTO
    * @return PageResult
    * @Date 2024/9/12 01:45
    */
    PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);
}
