package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.annotation.AutoFill;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.enumeration.OperationType;
import com.sky.vo.DishVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface DishMapper {

    /**
     * 根据分类id查询菜品数量
     * @param categoryId
     * @return
     */
    @Select("select count(id) from dish where category_id = #{categoryId}")
    Integer countByCategoryId(Long categoryId);

    /**
    * 新增菜品
    * @param dish
    * @return
    * @Date 2024/9/11 17:24
    */
    @AutoFill(value = OperationType.INSERT)
    void insert(Dish dish);

    /**
    * 菜品分页查询
    * @param dishPageQueryDTO
    * @return Page<DishVO>
    * @Date 2024/9/11 19:22
    */
    Page<DishVO> pageQuery(DishPageQueryDTO dishPageQueryDTO);

    /**
     * 根据id查询
     * @param id
     * @return Dish
     * @Date 2024/9/11 19:41
     */
    @Select("select * from dish where id = #{id}")
    Dish getById(Long id);

    /**
    * 根据id删除菜品
    * @param id 
    * @return 
    * @Date 2024/9/11 20:26
    */
    @Delete("delete from dish where id = #{id}")
    void deleteById(Long id);

    /**
    * 根据ids批量删除菜品
    * @param ids
    * @return
    * @Date 2024/9/11 20:52
    */
    void deleteByIdBatch(List<Long> ids);

    /**
    * 修改菜品
    * @param dish
    * @return
    * @Date 2024/9/11 22:19
    */
    @AutoFill(value = OperationType.UPDATE)
    void update(Dish dish);
}
