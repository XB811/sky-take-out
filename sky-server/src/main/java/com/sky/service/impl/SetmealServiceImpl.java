package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @FileName SetmealServiceImpl
 * @Description 套餐相关接口
 * @Author xb
 * @date 2024-09-12
 **/
@Service
@Slf4j
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;

    /**
    * 新增套餐
    * @param setmealDTO
    * @return
    * @Date 2024/9/12 01:00
    */
    @Override
    public void saveWithDish(SetmealDTO setmealDTO) {
        //向Setmeal中插入套餐
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.insert(setmeal);
        //向setmeal_dish表插入菜品
        Long setmealId =setmeal.getId();
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        if(dishes !=null && !dishes.isEmpty()){
            //批量插入setmealId
            dishes.forEach(setmealDish ->{
                setmealDish.setSetmealId(setmealId);
            });
            //批量插入菜品
            setmealDishMapper.insertBatch(dishes);
        }

    }

    /**
    * 套餐分页查询
    * @param setmealPageQueryDTO
    * @return PageResult
    * @Date 2024/9/12 01:46
    */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page<SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        Long total = page.getTotal();
        List<SetmealVO> records =page.getResult();
        return new PageResult(total,records);
    }
}
