package com.sky.mapper;

import com.sky.entity.OrderDetail;
import org.apache.ibatis.annotations.Mapper;

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
}
