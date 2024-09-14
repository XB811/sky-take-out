package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @FileName OrderDetailMapper
 * @Description
 * @Author xb
 * @date 2024-09-14
 **/
@Mapper
public interface OrderDetailMapper {
    void insert(OrderDetail orderDetail);

    void insertBatch(List<OrderDetail> orderDetailList);

    @Select("select * from order_detail where order_id =#{orderId}")
    List<OrderDetail> getByOrderId(Long orderId);
}
