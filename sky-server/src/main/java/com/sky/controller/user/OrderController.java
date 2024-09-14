package com.sky.controller.user;

import com.alibaba.fastjson.JSON;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.service.UserService;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @FileName OrderController
 * @Description
 * @Author xb
 * @date 2024-09-14
 **/
@RestController("userOrderController")
@RequestMapping("/user/order")
@Slf4j
@Api(tags = "C端-订单接口")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private WebSocketServer webSocketServer;

    /**
    * 用户下单
    * @param ordersSubmitDTO 
    * @return Result<OrderSubmitVO> 
    * @Date 2024/9/14 14:49
    */
    @PostMapping("/submit")
    @ApiOperation("用户下单")
    public Result<OrderSubmitVO> submit(@RequestBody OrdersSubmitDTO ordersSubmitDTO) {
        log.info("用户下单，参数为:{}", ordersSubmitDTO);
        OrderSubmitVO orderSubmitVO = orderService.submitOrder(ordersSubmitDTO);
        return Result.success(orderSubmitVO);
    }

//    /**
//     * 订单支付
//     *
//     * @param ordersPaymentDTO
//     * @return
//     */
//    @PutMapping("/payment")
//    @ApiOperation("订单支付")
//    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        log.info("订单支付：{}", ordersPaymentDTO);
//        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
//        log.info("生成预支付交易单：{}", orderPaymentVO);
//        return Result.success(orderPaymentVO);
//    }

    //跳过微信支付

    @PutMapping("/payment")
    @ApiOperation("支付订单")
    public Result<String> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("订单支付:{}", ordersPaymentDTO);
        orderService.payment(ordersPaymentDTO);
        return Result.success();
    }

    @PutMapping("/paySuccess")
    @ApiOperation("支付成功")
    public Result<String> paySuccess(@RequestBody String orderNumber){
        log.info("订单支付成功:{}", orderNumber);
        orderService.paySuccess(orderNumber);
        return Result.success(orderNumber);
    }

    /**
    * 历史订单查询
    * @param ordersPageQueryDTO
    * @return Result<PageResult>
    * @Date 2024/9/14 16:40
    */
    @GetMapping("/historyOrders")
    @ApiOperation("历史订单查询")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO) {
        Long userId = BaseContext.getCurrentId();
        ordersPageQueryDTO.setUserId(userId);
        log.info("历史订单查询:{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }
    /**
    * 查询订单详情
    * @param id
    * @return Result<OrderVO>
    * @Date 2024/9/14 19:04
    */
    @GetMapping("/orderDetail/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getById(@PathVariable Long id) {
        log.info("查询订单详情:{}",id);
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }

    /**
    * 取消订单
    * @param id
    * @return Result
    * @Date 2024/9/14 19:31
    */
    @PutMapping("/cancel/{id}")
    @ApiOperation("取消订单")
    public Result cancel(@PathVariable Long id) {
        log.info("取消订单:{}",id);
//        - 待支付和待接单状态下，用户可直接取消订单
//        - 商家已接单状态下，用户取消订单需电话沟通商家
//        - 派送中状态下，用户取消订单需电话沟通商家
//        - 如果在待接单状态下取消订单，需要给用户退款
//        - 取消订单后需要将订单状态修改为“已取消”
        //必须单独写services层
        orderService.userCancelById(id);
        return Result.success();
    }

    /**
    * 再来一单
    * @param id
    * @return Result
    * @Date 2024/9/14 19:31
    */
    @PostMapping("/repetition/{id}")
    @ApiOperation("再来一单")
    public Result repetition(@PathVariable Long id){
        log.info("再来一单:{}",id);
        orderService.repetition(id);
        return Result.success();
    }

    @GetMapping("/reminder/{id}")
    @ApiOperation("催单")
    public Result reminder(@PathVariable Long id){
        log.info("催单:{}",id);
        Map map = new HashMap();
        map.put("type",2);
        map.put("orderId",id);
        Orders orders = orderService.getById(id);
        map.put("content","客户催单，订单号："+orders.getNumber());
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
        return Result.success();
    }
}
