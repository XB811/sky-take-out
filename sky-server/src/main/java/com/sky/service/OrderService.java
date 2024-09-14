package com.sky.service;

import com.sky.dto.OrdersPaymentDTO;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.vo.OrderPaymentVO;
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


//    /**
//     * 订单支付
//     * @param ordersPaymentDTO
//     * @return
//     */
//    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception;
//
//    /**
//     * 支付成功，修改订单状态
//     * @param outTradeNo
//     */
//    void paySuccess(String outTradeNo);

    //跳过微信支付
    /**
    * 支付订单
    * @param ordersPaymentDTO
    * @return
    * @Date 2024/9/14 16:01
    */
    void payment(OrdersPaymentDTO ordersPaymentDTO);

    /**
    * 支付成功
    * @param orderNumber
    * @return
    * @Date 2024/9/14 16:02
    */
    void paySuccess(String orderNumber);
}
