package com.sky.controller.admin;

import com.sky.dto.OrdersCancelDTO;
import com.sky.dto.OrdersConfirmDTO;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.dto.OrdersRejectionDTO;
import com.sky.entity.Orders;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.OrderService;
import com.sky.vo.OrderStatisticsVO;
import com.sky.vo.OrderVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @FileName OrderController
 * @Description
 * @Author xb
 * @date 2024-09-14
 **/

@RestController("adminOrderController")
@RequestMapping("/admin/order")
@Slf4j
@Api(tags = "订单管理接口")
public class OrderController {
    @Autowired
    private OrderService orderService;

    /**
    * 订单搜索
    * @param ordersPageQueryDTO
    * @return Result<PageResult>
    * @Date 2024/9/14 20:09
    */
    @GetMapping("/conditionSearch")
    @ApiOperation("订单搜索")
    public Result<PageResult> pageResultResult(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("订单搜索:{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
    *
     各个状态的订单数量统计
    * @return Result<OrderStatisticsVO>
    * @Date 2024/9/14 20:10
    */
    @GetMapping("/statistics")
    @ApiOperation("各个状态的订单数量统计")
    public Result<OrderStatisticsVO> statistics(){
        OrderStatisticsVO orderStatisticsVO = orderService.statistics();
        return Result.success(orderStatisticsVO);
    }

    /**
    * 查询订单详情
    * @param id
    * @return Result<OrderVO>
    * @Date 2024/9/14 20:24
    */
    @GetMapping("/details/{id}")
    @ApiOperation("查询订单详情")
    public Result<OrderVO> getById(@PathVariable Long id) {
        log.info("查询订单详情:{}",id);
        OrderVO orderVO = orderService.getById(id);
        return Result.success(orderVO);
    }

    /**
    * 接单
    * @param ordersConfirmDTO
    * @return Result
    * @Date 2024/9/14 20:25
    */
    @PutMapping("/confirm")
    @ApiOperation("接单")
    public Result confirm(@RequestBody OrdersConfirmDTO ordersConfirmDTO) {
        log.info("接单:{}",ordersConfirmDTO);
        ordersConfirmDTO.setStatus(Orders.CONFIRMED);
        orderService.updateStatusById(ordersConfirmDTO);
        return Result.success();
    }

    /**
    * 拒单
    * @param ordersRejectionDTO 
    * @return Result 
    * @Date 2024/9/14 21:40
    */
    @PutMapping("/rejection")
    @ApiOperation("拒单")
    public Result rejection(@RequestBody OrdersRejectionDTO ordersRejectionDTO){
        log.info("拒单:{}",ordersRejectionDTO);
        orderService.rejection(ordersRejectionDTO);
        return Result.success();
    }
    
    @PutMapping("/cancel")
    @ApiOperation("取消订单")
    public Result cancel(@RequestBody OrdersCancelDTO ordersCancelDTO){
        log.info("取消订单:{}",ordersCancelDTO);
        orderService.adminCnacle(ordersCancelDTO);
        return Result.success();
    }

    /**
    * 派送订单
    * @param id 
    * @return Result 
    * @Date 2024/9/14 21:51
    */
    @PutMapping("/delivery/{id}")
    @ApiOperation("派送订单")
    public Result delivery(@PathVariable Long id){
        log.info("派送订单:{}",id);
        orderService.delivery(id);
        return Result.success();
    }
    /**
     * 完成订单
     *
     * @return
     */
    @PutMapping("/complete/{id}")
    @ApiOperation("完成订单")
    public Result complete(@PathVariable("id") Long id) {
        orderService.complete(id);
        return Result.success();
    }
}
