package com.sky.service;

import com.sky.dto.*;
import com.sky.result.PageResult;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;

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

    /**
    * 分页查询
    * @param ordersPageQueryDTO
    * @return PageResult
    * @Date 2024/9/14 16:43
    */
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
    * 查询订单详情
    * @param id
    * @return OrderVO
    * @Date 2024/9/14 19:04
    */
    OrderVO getById(Long id);

    void cancle(Long id);

    void repetition(Long id);

    OrderStatisticsVO statistics();

    void updateStatusById(OrdersConfirmDTO ordersConfirmDTO);

    void rejection(OrdersRejectionDTO order);

    void userCancelById(Long id);

    void adminCnacle(OrdersCancelDTO ordersCancelDTO);

    void delivery(Long id);
    /**
     * 完成订单
     *
     * @param id
     */
    void complete(Long id);

    void reminder(Long id);
}
