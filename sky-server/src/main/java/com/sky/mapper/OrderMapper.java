package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.GoodsSalesDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import com.sky.vo.SalesTop10ReportVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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

    /**
    * 分页查询订单
    * @param ordersPageQueryDTO
    * @return Page<Orders>
    * @Date 2024/9/14 16:59
    */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
    * 根据订单id获取
    * @param id
    * @return Orders
    * @Date 2024/9/14 19:07
    */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);

    @Select("select count(*) from orders where status = #{status}")
    Integer countByStatus(Integer status);

    /**
    * 根据订单状态和订单下单时间查询订单
    * @param status 
     * @param orderTime 
    * @return List<Orders> 
    * @Date 2024/9/14 22:47
    */
    @Select("select * from orders where status =#{status} and order_time < #{orderTime} ")
    List<Orders> getByStatusAndOrderTimeLT(Integer status, LocalDateTime orderTime);

    /**
    * 根据条件动态查找数据
    * @param map
    * @return 
    * @Date 2024/9/15 00:59
    */

    Double sumByMap(Map map);

    Integer countByMap(Map map);

    /**
    * 统计一定时间内的销量前10
    * @param begin
     * @param end
    * @return List<SalesTop10ReportVO>
    * @Date 2024/9/15 02:09
    */
    List<GoodsSalesDTO> getSalesTop10(LocalDateTime begin, LocalDateTime end);
}
