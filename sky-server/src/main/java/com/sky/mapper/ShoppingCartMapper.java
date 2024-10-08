package com.sky.mapper;

import com.sky.entity.ShoppingCart;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * @FileName ShoppingCartMapper
 * @Description
 * @Author xb
 * @date 2024-09-14
 **/
@Mapper
public interface ShoppingCartMapper {

    //动态条件查询
    List<ShoppingCart> list(ShoppingCart shoppingCart);
    //根据id修改商品数量
    @Update("update shopping_cart set number = #{number} where id = #{id}")
    void update(ShoppingCart cart);
    //插入数据
    @Insert("insert into shopping_cart (name, image, user_id, dish_id, setmeal_id, dish_flavor," +
            "number, amount, create_time) " +
            "values (#{name},#{image},#{userId},#{dishId},#{setmealId},#{dishFlavor}," +
            "#{number},#{amount},#{createTime})")
    void insert(ShoppingCart shoppingCart);

    //根据userId删除购物车
    @Delete("delete from shopping_cart where user_id = #{userId}")
    void deleteByUserId(Long userId);
    //根据id删除购物车
    @Delete("delete from shopping_cart where id = #{id}")
    void deleteById(ShoppingCart cart);

    void insertBatch(List<ShoppingCart> shoppingCartList);
}
