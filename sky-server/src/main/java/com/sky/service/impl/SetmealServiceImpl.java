package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.exception.SetmealEnableFailedException;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.DishItemVO;
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
    @Autowired
    private DishMapper dishMapper;

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

    /**
    * 根据ids批量删除套餐
    * @param ids 
    * @return 
    * @Date 2024/9/12 11:55
    */
    @Override
    public void deleteBatch(List<Long> ids) {
        //判读当前套餐是否能够删除---是否存在起售中的套餐
        for(Long id : ids){
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.SETMEAL_ON_SALE);
            }
        }

        //根据ids删除套餐
        setmealMapper.deleteByIdBatch(ids);
        //删除套餐关联的菜品信息
        setmealDishMapper.deleteBySetmealIdBatch(ids);
    }

    /**
    * 根据id查询套餐
    * @param id 
    * @return SetmealVO 
    * @Date 2024/9/12 11:55
    */
    @Override
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        List<SetmealDish> setmealDishes =setmealDishMapper.getBySetmealId(id);
        SetmealVO setmealVO = new SetmealVO();
        BeanUtils.copyProperties(setmeal,setmealVO);
        setmealVO.setSetmealDishes(setmealDishes);
        return setmealVO;
    }

    /**
    * 更新套餐
    * @param setmealDTO 
    * @return 
    * @Date 2024/9/13 22:28
    */
    @Override
    public void updateWithDish(SetmealDTO setmealDTO) {
        //更新套餐信息
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO, setmeal);
        setmealMapper.update(setmeal);
        //删除原来的套餐菜品
        setmealDishMapper.deleteBySetmealId(setmeal.getId());
        //添加新的套餐菜品
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        if(dishes !=null && !dishes.isEmpty()){
            //批量插入setmealId
            dishes.forEach(setmealDish ->{
                setmealDish.setSetmealId(setmeal.getId());
            });
            //批量插入菜品
            setmealDishMapper.insertBatch(dishes);
        }
    }

    /**
    * 启售停售套餐
    * @param status 
     * @param id 
    * @return 
    * @Date 2024/9/13 22:28
    */
    @Override
    public void startOrStop(Integer status, Long id) {
        //如果status为1，判读当前套餐下是否有菜品的status为0
        if(status == StatusConstant.ENABLE){
            //获取套餐id包含的菜品id
            List<SetmealDish> dishes=setmealDishMapper.getBySetmealId(id);
            if(dishes !=null && !dishes.isEmpty()){
                for(SetmealDish setmealDish : dishes){
                    Dish dish=dishMapper.getById(setmealDish.getDishId());
                    if(dish.getStatus()==StatusConstant.DISABLE)
                        throw new SetmealEnableFailedException(MessageConstant.SETMEAL_ENABLE_FAILED);
                }
            }
        }
        Setmeal setmeal = Setmeal.builder()
                .status(status)
                .id(id)
                .build();
        setmealMapper.update(setmeal);
    }

    /**
     * 条件查询
     * @param setmeal
     * @return
     */
    public List<Setmeal> list(Setmeal setmeal) {
        List<Setmeal> list = setmealMapper.list(setmeal);
        return list;
    }

    /**
     * 根据id查询菜品选项
     * @param id
     * @return
     */
    public List<DishItemVO> getDishItemById(Long id) {
        return setmealMapper.getDishItemBySetmealId(id);
    }
}
