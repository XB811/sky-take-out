package com.sky.service;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.result.PageResult;
import com.sky.vo.DishItemVO;
import com.sky.vo.SetmealVO;

import java.util.List;

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

    /**
    * 批量删除套餐
    * @param ids
    * @return
    * @Date 2024/9/12 11:30
    */
    void deleteBatch(List<Long> ids);

    /**
    * 根据id查询菜品
    * @param id
    * @return SetmealVO
    * @Date 2024/9/12 11:50
    */
    SetmealVO getById(Long id);

    /**
    * 修改套餐
    * @param setmealDTO
    * @return
    * @Date 2024/9/12 12:01
    */
    void updateWithDish(SetmealDTO setmealDTO);

    /**
    * 套餐启售、停售
    * @param status
     * @param id
    * @return
    * @Date 2024/9/12 12:43
    */
    void startOrStop(Integer status, Long id);

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
