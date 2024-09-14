package com.sky.mapper;

import com.sky.entity.Orders;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

/**
* @FileName OrderMapper
* @Description 
* @Author xb
* @date 2024-09-14
**/
@Mapper
public interface OrderMapper {
    /**
    * 插入订单
    * @param orders 
    * @return 
    * @Date 2024/9/14 14:51
    */
    void insert(Orders orders);

    /**
     * 根据订单号查询订单
     * @param number
     */
    @Select("select * from orders where number = #{number}")
    Orders getByNumber(String number);

    /**
     * 修改订单信息
     * @param orders
     */
    void update(Orders orders);
}
