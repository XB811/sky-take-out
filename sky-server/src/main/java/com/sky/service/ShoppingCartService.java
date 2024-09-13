package com.sky.service;

import com.sky.dto.ShoppingCartDTO;
import com.sky.entity.ShoppingCart;

import java.util.List;

/**
 * @FileName ShoppingCartService
 * @Description C端-购物车接口
 * @Author xb
 * @date 2024-09-14
 **/

public interface ShoppingCartService {
    /**
    * 添加购物车
    * @param shoppingCartDTO
    * @return
    * @Date 2024/9/14 01:30
    */
    void save(ShoppingCartDTO shoppingCartDTO);

    /**
    * 显示购物车内所有产品
    * @return List<ShoppingCartDTO>
    * @Date 2024/9/14 02:16
    */
    List<ShoppingCart> showShoppingCart();
}
