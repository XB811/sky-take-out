package com.sky.service.impl;

import com.sky.constant.MessageConstant;
import com.sky.context.BaseContext;
import com.sky.dto.OrdersSubmitDTO;
import com.sky.entity.AddressBook;
import com.sky.entity.OrderDetail;
import com.sky.entity.Orders;
import com.sky.entity.ShoppingCart;
import com.sky.exception.AddressBookBusinessException;
import com.sky.exception.ShoppingCartBusinessException;
import com.sky.mapper.AddressBookMapper;
import com.sky.mapper.OrderDetailMapper;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.ShoppingCartMapper;
import com.sky.service.OrderService;
import com.sky.vo.OrderSubmitVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
//        orders.setAddress(addressBook.get);
//        orders.setUserName();
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
}
