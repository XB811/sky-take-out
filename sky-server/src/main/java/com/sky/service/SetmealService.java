package com.sky.service;

import com.sky.dto.SetmealDTO;

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
}
