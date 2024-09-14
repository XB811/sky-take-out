package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.util.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @FileName ReportServiceImpl
 * @Description
 * @Author xb
 * @date 2024-09-15
 **/
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Autowired
    OrderMapper orderMapper;
    @Autowired
    UserMapper userMapper;

    @Override
    public TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end) {
        //从begin到end范围内的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
            dateList.add(begin);
        }
        List<Double> turnoverList = new ArrayList<>();
        for(LocalDate date : dateList){
            //状态为已完成订单的合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map =new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            map.put("status",Orders.COMPLETED);
            Double turnover = orderMapper.sumByMap(map);
            turnover = turnover == null ? 0.0 : turnover;
            turnoverList.add(turnover);
        }
        return TurnoverReportVO.builder()
                .dateList(StringUtils.join(dateList,','))
                .turnoverList(StringUtils.join(turnoverList,','))
                .build();
    }

    /**
    * 根据开始时间和结束时间计算每日新增人数
    * @param begin 
     * @param end 
    * @return UserReportVO 
    * @Date 2024/9/15 01:26
    */
    @Override
    public UserReportVO getUserStatistics(LocalDate begin, LocalDate end) {
        //从begin到end范围内的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
            dateList.add(begin);
        }
        List<Integer> newUserList = new ArrayList<>();
        List<Integer> totalUserList= new ArrayList<>();
        Integer total =0;
        for(LocalDate date : dateList){
            //状态为已完成订单的合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map =new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer number =userMapper.sumByMap(map);
            newUserList.add(number);
            total+=number;
            totalUserList.add(total);
        }
        return UserReportVO.builder()
                .dateList(StringUtils.join(dateList,','))
                .newUserList(StringUtils.join(newUserList,','))
                .totalUserList(StringUtils.join(totalUserList,','))
                .build();
    }

    @Override
    public OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end) {
        //从begin到end范围内的所有日期
        List<LocalDate> dateList = new ArrayList<>();
        dateList.add(begin);

        while (!begin.equals(end)){
            begin = begin.plusDays(1);//日期计算，获得指定日期后1天的日期
            dateList.add(begin);
        }
        List<Integer> orderCountList = new ArrayList<>();
        List<Integer> validOrderCountList = new ArrayList<>();
        Integer totalOrderCount=0;
        Integer validOrderCount=0;
        for(LocalDate date : dateList) {
            //状态为已完成订单的合计
            LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
            LocalDateTime endTime = LocalDateTime.of(date, LocalTime.MAX);
            Map map =new HashMap<>();
            map.put("begin",beginTime);
            map.put("end",endTime);
            Integer totalOrder = orderMapper.countByMap(map);
            map.put("status",Orders.COMPLETED);
            Integer validOrder =orderMapper.countByMap(map);
            orderCountList.add(totalOrder);
            validOrderCountList.add(validOrder);
            totalOrderCount+=totalOrder;
            validOrderCount+=validOrder;
        }
        Double orderCompletionRate =(double)validOrderCount/totalOrderCount;
        return OrderReportVO.builder()
                .dateList(StringUtils.join(dateList,','))
                .orderCountList(StringUtils.join(orderCountList,','))
                .validOrderCountList(StringUtils.join(validOrderCountList,','))
                .totalOrderCount(totalOrderCount)
                .validOrderCount(validOrderCount)
                .orderCompletionRate(orderCompletionRate)
                .build();
    }
    /**
     * 统计指定时间内的效率前10
     * @param begin
     * @param end
     * @return SalesTop10ReportVO
     * @Date 2024/9/15 01:57
     */
    @Override
    public SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end) {

        LocalDateTime beginTime = LocalDateTime.of(begin, LocalTime.MIN);
        LocalDateTime endTime = LocalDateTime.of(end, LocalTime.MAX);
        List<GoodsSalesDTO> goodsSalesDTOList = orderMapper.getSalesTop10(beginTime, endTime);
        List<String> nameList = new ArrayList<>();
        List<Integer> numberList = new ArrayList<>();
        for(GoodsSalesDTO goodsSalesDTO : goodsSalesDTOList){
            nameList.add(goodsSalesDTO.getName());
            numberList.add(goodsSalesDTO.getNumber());
        }
        return  SalesTop10ReportVO.builder()
                .nameList(StringUtils.join(nameList,','))
                .numberList(StringUtils.join(numberList,','))
                .build();
    }
}
