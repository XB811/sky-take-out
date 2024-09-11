package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * @FileName DishServiceImpl
 * @Description
 * @Author xb
 * @date 2024-09-11
 **/
@Service
@Slf4j
public class DishServiceImpl implements DishService {

    @Autowired
    DishMapper dishMapper;
    @Autowired
    DishFlavorMapper dishFlavorMapper;
    @Autowired
    SetmealDishMapper setmealDishMapper;

    /**
    * 新增菜品，同时添加口味
    * @param dishDTO
    * @return
    * @Date 2024/9/11 17:16
    */
    @Override
    public void saveWithFlavor(DishDTO dishDTO) {
        //向dish表中插入菜品
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.insert(dish);
        //向dish_flavor表中插入口味
        Long dishId = dish.getId();
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()) {
            //批量插入dishId
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            //插入数条口味信息
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
    * 菜品分页查询
    * @param dishPageQueryDTO
    * @return PageResult
    * @Date 2024/9/11 19:22
    */
    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        Long total = page.getTotal();
        List<DishVO> records = page.getResult();
        return new PageResult(total, records);
    }

    /**
    * 批量删除菜品
    * @param ids
    * @return
    * @Date 2024/9/11 19:22
    */
    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        //判读当前菜品是否能够删除---是否存在启售中的菜品
        for(Long id : ids) {
            Dish dish =dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE) {
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判读当前菜品是否能够删除---是否存在被套餐关联的菜品
        List<Long> setmealIds = setmealDishMapper.getSetmealDishByDishIds(ids);
        if(setmealIds != null && !setmealIds.isEmpty()) {
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);
        }
        /*
        for(Long id : ids) {
            //删除菜品数据
            dishMapper.deleteById(id);
            //删除口味数据
            dishFlavorMapper.deleteByDishId(id);
        }
        */
        //批量删除菜品数据
        dishMapper.deleteByIdBatch(ids);
        //批量删除口味数据
        dishFlavorMapper.deleteByDishIdBatch(ids);
    }

    /**
    * 根据id查询菜品
    * @param id 
    * @return DishVO 
    * @Date 2024/9/11 21:45
    */
    @Override
    public DishVO getById(Long id) {
        Dish dish = dishMapper.getById(id);
        List<DishFlavor> dishFlavors= dishFlavorMapper.getByDishId(id);
        DishVO dishVO = new DishVO();
        BeanUtils.copyProperties(dish,dishVO);
        dishVO.setFlavors(dishFlavors);
        return dishVO;
    }

    /**
    * 修改菜品
    * @param dishDTO
    * @return
    * @Date 2024/9/11 21:46
    */
    @Override
    public void updateWithFlavor(DishDTO dishDTO) {
        //更新菜品信息
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO, dish);
        dishMapper.update(dish);
        //删除原来的菜品口味
        dishFlavorMapper.deleteByDishId(dish.getId());
        //添加新的菜品口味
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()) {
            //批量插入dishId
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dish.getId());
            });
            //插入数条口味信息
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    /**
    * 菜品起售、停售
    * @param status 
     * @param id 
    * @return 
    * @Date 2024/9/11 23:32
    */
    @Override
    public void startOrStop(Integer status, Long id) {
        Dish dish = Dish.builder()
                .status(status)
                .id(id)
                .build();
        dishMapper.update(dish);
    }
}
