package com.sky.mapper;

import com.sky.entity.Orders;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.annotations.Mapper;

/**
* @FileName OrderMapper
* @Description 
* @Author xb
* @date 2024-09-14
**/
@Mapper
public interface OrderMapper {
    void insert(Orders orders);
}
