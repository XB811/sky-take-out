package com.sky.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.*;
import com.sky.entity.*;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.OrderBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.*;
import com.sky.result.PageResult;
import com.sky.service.OrderService;
import com.sky.utils.WeChatPayUtil;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderSubmitVO;
import com.sky.vo.OrderVO;
import com.sky.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @FileName OrderServiceImpl
 * @Description
 * @Author xb
 * @date 2024-09-14
 **/
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderDetailMapper orderDetailMapper;
    @Autowired
    private ShoppingCartMapper shoppingCartMapper;
    @Autowired
    private AddressBookMapper addressBookMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private WeChatPayUtil weChatPayUtil;
    @Autowired
    private WebSocketServer webSocketServer;
    /**
    * 用户下单
    * @param ordersSubmitDTO 
    * @return OrderSubmitVO 
    * @Date 2024/9/14 14:50
    */
    @Override
    @Transactional
    public OrderSubmitVO submitOrder(OrdersSubmitDTO ordersSubmitDTO) {
        //处理地址为空的异常
        AddressBook addressBook = addressBookMapper.getById(ordersSubmitDTO.getAddressBookId());
        if(addressBook == null){
            //抛出地址篇为空的异常
            throw new AddressBookBusinessException(MessageConstant.ADDRESS_BOOK_IS_NULL);
        }
        //处理购物车为空的异常
        Long userId = BaseContext.getCurrentId();
        User user = userMapper.getById(userId);
        ShoppingCart shoppingCart = ShoppingCart.builder().userId(userId).build();
        List<ShoppingCart> list = shoppingCartMapper.list(shoppingCart);
        if(list == null || list.size() == 0){
            throw new ShoppingCartBusinessException(MessageConstant.SHOPPING_CART_IS_NULL);
        }
        //向订单表插入一条数据
        Orders orders = new Orders();
        BeanUtils.copyProperties(ordersSubmitDTO, orders);
        orders.setNumber(String.valueOf(System.currentTimeMillis()));
        orders.setStatus(Orders.PENDING_PAYMENT);
        orders.setUserId(userId);
        orders.setOrderTime(LocalDateTime.now());
        orders.setPayStatus(Orders.UN_PAID);
        orders.setPhone(addressBook.getPhone());
        String address =addressBook.getProvinceName()+addressBook.getCityName()+addressBook.getDistrictName()+addressBook.getDetail();
        orders.setAddress(address);
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orderMapper.insert(orders);
        //向订单明细表插入n条数据
        List<OrderDetail> orderDetailList = new ArrayList<>();
        for(ShoppingCart cart : list){
            OrderDetail orderDetail =new OrderDetail();
            BeanUtils.copyProperties(cart, orderDetail);//订单明细
            orderDetail.setOrderId(orders.getId());//订单id
//            orderDetailMapper.insert(orderDetail);
            orderDetailList.add(orderDetail);
        }
        orderDetailMapper.insertBatch(orderDetailList);//批量插入订单详情
        //清空购物车
        shoppingCartMapper.deleteByUserId(userId);
        //返回OrderSubmitVO
        OrderSubmitVO orderSubmitVO =OrderSubmitVO.builder()
                .id(orders.getId())
                .orderNumber(orders.getNumber())
                .orderAmount(orders.getAmount())
                .orderTime(orders.getOrderTime())
                .build();
        return orderSubmitVO;
    }




//    /**
//     * 订单支付
//     *
//     * @param ordersPaymentDTO
//     * @return
//     */
//    public OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO) throws Exception {
//        // 当前登录用户id
//        Long userId = BaseContext.getCurrentId();
//        User user = userMapper.getById(userId);
//
//        //调用微信支付接口，生成预支付交易单
//        JSONObject jsonObject = weChatPayUtil.pay(
//                ordersPaymentDTO.getOrderNumber(), //商户订单号
//                new BigDecimal(0.01), //支付金额，单位 元
//                "苍穹外卖订单", //商品描述
//                user.getOpenid() //微信用户的openid
//        );
//
//        if (jsonObject.getString("code") != null && jsonObject.getString("code").equals("ORDERPAID")) {
//            throw new OrderBusinessException("该订单已支付");
//        }
//
//        OrderPaymentVO vo = jsonObject.toJavaObject(OrderPaymentVO.class);
//        vo.setPackageStr(jsonObject.getString("package"));
//
//        return vo;
//    }
//
//    /**
//     * 支付成功，修改订单状态
//     *
//     * @param outTradeNo
//     */
//    public void paySuccess(String outTradeNo) {
//
//        // 根据订单号查询订单
//        Orders ordersDB = orderMapper.getByNumber(outTradeNo);
//
//        // 根据订单id更新订单的状态、支付方式、支付状态、结账时间
//        Orders orders = Orders.builder()
//                .id(ordersDB.getId())
//                .status(Orders.TO_BE_CONFIRMED)
//                .payStatus(Orders.PAID)
//                .checkoutTime(LocalDateTime.now())
//                .build();
//
//        orderMapper.update(orders);
//    }


    //跳过微信支付
    /**
    * 支付成功
    * @param ordersPaymentDTO
    * @return
    * @Date 2024/9/14 16:05
    */
    @Override
    //由于跳过了微信支付，这里只校验是否存在订单和订单是否已经支付
    public void payment(OrdersPaymentDTO ordersPaymentDTO) {
        //查询订单状态，若已经支付则抛出异常
        String number = ordersPaymentDTO.getOrderNumber();//获取订单号
        Orders order = orderMapper.getByNumber(number);
        if(order != null){
            if(order.getPayStatus() == Orders.PAID){
                throw new OrderBusinessException(MessageConstant.ORDER_ALREADY_PAID);//订单已经支付，抛出异常
            }
        }else{
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
    }

    /**
    * 支付成功
    * @param orderNumber
    * @return
    * @Date 2024/9/14 16:02
    */
    @Override
    public void paySuccess(String orderNumber) {
        //根据订单号查询order
        Orders order = orderMapper.getByNumber(orderNumber);
        if(order != null){
            order.setCheckoutTime(LocalDateTime.now());//结账时间
            order.setPayStatus(Orders.PAID);//支付状态：已经支付
            order.setStatus(Orders.TO_BE_CONFIRMED);//订单状态：待接单
            orderMapper.update(order);

            //支付成功后通知管理端
            Map map =new HashMap<>();
            map.put("type",1);
            map.put("orderId",order.getId());
            map.put("content","订单号："+orderNumber);
            String json = JSON.toJSONString(map);
            webSocketServer.sendToAllClient(json);
        }
    }

    /**
    * 分页查询
    * @param ordersPageQueryDTO
    * @return PageResult
    * @Date 2024/9/14 19:54
    */
    @Override
    public PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO) {
        //开始分页查询
        PageHelper.startPage(ordersPageQueryDTO.getPage(),ordersPageQueryDTO.getPageSize());
        Page<Orders> page = orderMapper.pageQuery(ordersPageQueryDTO);
        Long total = page.getTotal();
        List<Orders> list = new ArrayList<>();

        // 查询出订单明细，并封装入OrderVO进行响应
        if (page != null && page.getTotal() > 0) {
            for (Orders orders : page) {
                Long orderId = orders.getId();// 订单id

                // 查询订单明细
                List<OrderDetail> orderDetails = orderDetailMapper.getByOrderId(orderId);
                //OrderVO继承了Orders，有Orders的所有属性和方法
                OrderVO orderVO = new OrderVO();
                BeanUtils.copyProperties(orders, orderVO);
                orderVO.setOrderDetailList(orderDetails);
                list.add(orderVO);
            }
        }
//        log.info("{}", list);
//        log.info("{}", new PageResult(total,list));
        return new PageResult(total,list);
    }

    /**
    * 查询订单详情
    * @param id
    * @return OrderVO
    * @Date 2024/9/14 19:04
    */
    @Override
    public OrderVO getById(Long id) {
        Orders order =orderMapper.getById(id);
        if(order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        OrderVO orderVO = new OrderVO();
        BeanUtils.copyProperties(order, orderVO);
        orderVO.setOrderDetailList(orderDetailMapper.getByOrderId(order.getId()));
        return orderVO;
    }

    /**
    * 取消订单
    * @param id
    * @return
    * @Date 2024/9/14 19:54
    */
    @Override
    public void cancle(Long id) {
        Orders order =orderMapper.getById(id);
        if(order == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        order.setStatus(Orders.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);
    }
    /**
    * 根据id更新状态
    * @param ordersConfirmDTO 
    * @return 
    * @Date 2024/9/14 21:06
    */
    @Override
    public void updateStatusById(OrdersConfirmDTO ordersConfirmDTO){
        //如果order为空，或者orderid为空
        Orders order =new Orders();
        BeanUtils.copyProperties(ordersConfirmDTO, order);
        if(order == null || order.getId() == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        orderMapper.update(order);
    }

    /**
    * 拒单
    * @param ordersRejectionDTO
    * @return
    * @Date 2024/9/14 20:48
    */
    @Override
    public void rejection(OrdersRejectionDTO ordersRejectionDTO) {

        Orders order1 = orderMapper.getById(ordersRejectionDTO.getId());
        // 校验订单是否存在
        if(order1 == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        if(order1.getStatus() !=Orders.TO_BE_CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders order = new Orders();
        order.setId(ordersRejectionDTO.getId());
        order.setRejectionReason(ordersRejectionDTO.getRejectionReason());
        //如果已经完成支付，则要先退款
        if(order1.getPayStatus() == Orders.PAID){
            //调用微信支付退款
//            String refund = null;
            //跳过微信支付功能
//            try {
//                refund = weChatPayUtil.refund(
//                        order1.getNumber(),
//                        order1.getNumber(),
//                        order1.getAmount(),
//                        order1.getAmount());
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }

            log.info("商家拒单-退款");
            order.setPayStatus(Orders.REFUND);
        }
        order.setStatus(Orders.CANCELLED);
        order.setCancelTime(LocalDateTime.now());
        orderMapper.update(order);

    }

    /**
    * 用户取消订单
    * @param id 
    * @return 
    * @Date 2024/9/14 21:48
    */
    @Override
    public void userCancelById(Long id)  {
//        - 待支付和待接单状态下，用户可直接取消订单
//        - 商家已接单状态下，用户取消订单需电话沟通商家
//        - 派送中状态下，用户取消订单需电话沟通商家
//        - 如果在待接单状态下取消订单，需要给用户退款
//        - 取消订单后需要将订单状态修改为“已取消”
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }

        //订单状态 1待付款 2待接单 3已接单 4派送中 5已完成 6已取消
        if (ordersDB.getStatus() > 2) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getStatus().equals(Orders.TO_BE_CONFIRMED)) {
            //调用微信支付退款接口
//            try {
//                weChatPayUtil.refund(
//                        ordersDB.getNumber(), //商户订单号
//                        ordersDB.getNumber(), //商户退款单号
//                        new BigDecimal(0.01),//退款金额，单位 元
//                        new BigDecimal(0.01));//原订单金额
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }

            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }

        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason("用户取消");
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
    * 管理端取消订单
    * @param ordersCancelDTO 
    * @return 
    * @Date 2024/9/14 21:42
    */
    @Override
    public void adminCnacle(OrdersCancelDTO ordersCancelDTO) {
//        - 取消订单其实就是将订单状态修改为“已取消”
//        - 商家取消订单时需要指定取消原因
//        - 商家取消订单时，如果用户已经完成了支付，需要为用户退款
        //根据id获取order
        Orders ordersDB = orderMapper.getById(ordersCancelDTO.getId());
        // 校验订单是否存在
        if (ordersDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Orders orders = new Orders();
        orders.setId(ordersDB.getId());

        // 订单处于待接单状态下取消，需要进行退款
        if (ordersDB.getPayStatus() == Orders.PAID) {
            //调用微信支付退款接口
//            try {
//                weChatPayUtil.refund(
//                        ordersDB.getNumber(), //商户订单号
//                        ordersDB.getNumber(), //商户退款单号
//                        new BigDecimal(0.01),//退款金额，单位 元
//                        new BigDecimal(0.01));//原订单金额
//            } catch (Exception e) {
//                throw new RuntimeException(e);
//            }
            log.info("商家取消订单-退款");
            //支付状态修改为 退款
            orders.setPayStatus(Orders.REFUND);
        }
        // 更新订单状态、取消原因、取消时间
        orders.setStatus(Orders.CANCELLED);
        orders.setCancelReason(ordersCancelDTO.getCancelReason());
        orders.setCancelTime(LocalDateTime.now());
        orderMapper.update(orders);
    }

    /**
    * 派送订单
    * @param id 
    * @return 
    * @Date 2024/9/14 21:51
    */
    @Override
    public void delivery(Long id) {
//        - 派送订单其实就是将订单状态修改为“派送中”
//        - 只有状态为“待派送”的订单可以执行派送订单操作
        Orders orderDB = orderMapper.getById(id);
        //订单不存在
        if (orderDB == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        //订单状态不为待派送/已接单
        if(orderDB.getStatus() != Orders.CONFIRMED){
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Orders orders = new Orders();
        orders.setId(orderDB.getId());
        orders.setStatus(Orders.DELIVERY_IN_PROGRESS);
        orderMapper.update(orders);
    }

    /**
    * 再来一单
    * @param id
    * @return
    * @Date 2024/9/14 19:54
    */
    @Override
    public void repetition(Long id) {
        // 查询当前用户id
        Long userId = BaseContext.getCurrentId();

        // 根据订单id查询当前订单详情
        List<OrderDetail> orderDetailList = orderDetailMapper.getByOrderId(id);

        // 将订单详情对象转换为购物车对象
        List<ShoppingCart> shoppingCartList = orderDetailList.stream().map(x -> {
            ShoppingCart shoppingCart = new ShoppingCart();

            // 将原订单详情里面的菜品信息重新复制到购物车对象中
            BeanUtils.copyProperties(x, shoppingCart, "id");
            shoppingCart.setUserId(userId);
            shoppingCart.setCreateTime(LocalDateTime.now());

            return shoppingCart;
        }).collect(Collectors.toList());

        // 将购物车对象批量添加到数据库
        shoppingCartMapper.insertBatch(shoppingCartList);

    }

    /**
    * 统计待接单、已接单、派送中的订单数量
    * @return OrderStatisticsVO
    * @Date 2024/9/14 20:17
    */
    @Override
    public OrderStatisticsVO statistics() {
        OrderStatisticsVO orderStatisticsVO = new OrderStatisticsVO();
        orderStatisticsVO.setToBeConfirmed(orderMapper.countByStatus(Orders.TO_BE_CONFIRMED));
        orderStatisticsVO.setConfirmed(orderMapper.countByStatus(Orders.CONFIRMED));
        orderStatisticsVO.setDeliveryInProgress(orderMapper.countByStatus(Orders.DELIVERY_IN_PROGRESS));
        return orderStatisticsVO;
    }
    /**
     * 完成订单
     *
     * @param id
     */
    public void complete(Long id) {
        // 根据id查询订单
        Orders ordersDB = orderMapper.getById(id);

        // 校验订单是否存在，并且状态为4
        if (ordersDB == null || !ordersDB.getStatus().equals(Orders.DELIVERY_IN_PROGRESS)) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }

        Orders orders = new Orders();
        orders.setId(ordersDB.getId());
        // 更新订单状态,状态转为完成
        orders.setStatus(Orders.COMPLETED);
        orders.setDeliveryTime(LocalDateTime.now());

        orderMapper.update(orders);
    }

    @Override
    public void reminder(Long id) {
        Orders orders = orderMapper.getById(id);
        if(orders == null){
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        Map map = new HashMap();
        map.put("type",2);
        map.put("orderId",id);
        map.put("content","客户催单，订单号："+orders.getNumber());
//        log.info("{}",map);
        String json = JSON.toJSONString(map);
        webSocketServer.sendToAllClient(json);
    }
}
