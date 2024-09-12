package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.enumeration.OperationType;
import com.sky.vo.SetmealVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealMapper {


     

    /**
     * 根据分类id查询套餐的数量
     * @param id
     * @return
     */
    @Select("select count(id) from setmeal where category_id = #{categoryId}")
    Integer countByCategoryId(Long id);

    /**
    * 新增套餐
    * @param setmeal
    * @return
    * @Date 2024/9/12 01:03
    */
    @AutoFill(value = OperationType.INSERT)
    void insert(Setmeal setmeal);

    /**
    * 套餐分页查询
    * @param setmealPageQueryDTO
    * @return Page<SetmealVO>
    * @Date 2024/9/12 01:49
    */
    Page<SetmealVO> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO);

    /**
    * 根据id获取套餐信息
    * @param id 
    * @return Setmeal 
    * @Date 2024/9/12 11:33
    */
    @Select("select * from setmeal where id = #{id}")
    Setmeal getById(Long id);

    /**
    * 根据ids批量删除套餐
    * @param ids
    * @return
    * @Date 2024/9/12 11:40
    */
    void deleteByIdBatch(List<Long> ids);

    /**
    * 修改套餐
    * @param setmeal
    * @return
    * @Date 2024/9/12 12:03
    */
    @AutoFill(value = OperationType.UPDATE)
    void update(Setmeal setmeal);
}
