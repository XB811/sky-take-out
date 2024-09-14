package com.sky.service;

import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderSubmitVO;

/**
 * @FileName OrderService
 * @Description
 * @Author xb
 * @date 2024-09-14
 **/

public interface OrderService {
    //提交订单
    OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO);
}
