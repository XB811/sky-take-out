package com.sky.service.impl;

import com.sky.dto.GoodsSalesDTO;
import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import com.sky.mapper.UserMapper;
import com.sky.service.ReportService;
import com.sky.service.WorkspaceService;
import com.sky.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.xssf.usermodel.XSSFAnchor;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
    @Autowired
    WorkspaceService workspaceService;

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
            Integer number =userMapper.countByMap(map);
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

    /**
    * 导出Excel报表接口
    * @param response 
    * @return 
    * @Date 2024/9/15 03:31
    */
    @Override
    public void exportBusinessData(HttpServletResponse response) {
        //1、查询数据库，获取营业数据--查询最近30天的数据
        LocalDate dateBegin = LocalDate.now().minusDays(30);
        LocalDate dateEnd = LocalDate.now().minusDays(1);
        BusinessDataVO businessData = workspaceService.getBusinessData(
                LocalDateTime.of(dateBegin, LocalTime.MIN),
                LocalDateTime.of(dateEnd, LocalTime.MAX));

        //2、通过poi将数据写入excel文件中
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("template/运营数据报表模板.xlsx");//获得项目下的resources资源文件
        try {
            XSSFWorkbook excel = new XSSFWorkbook(in);
            //获取表格标签页
            XSSFSheet sheet = excel.getSheet("Sheet1");
            //填充时间
            sheet.getRow(1).getCell(1).setCellValue("时间："+dateBegin+"至"+dateEnd);
            XSSFRow row = sheet.getRow(3);
            row.getCell(2).setCellValue(businessData.getTurnover());
            row.getCell(4).setCellValue(businessData.getOrderCompletionRate());
            row.getCell(6).setCellValue(businessData.getNewUsers());
            row = sheet.getRow(4);
            row.getCell(2).setCellValue(businessData.getValidOrderCount());
            row.getCell(4).setCellValue(businessData.getUnitPrice());
            //写入每日详细信息
            for(int i=0;i<30;i++){
                row = sheet.getRow(7+i);
                LocalDate today = dateBegin.plusDays(i);
                BusinessDataVO businessData1 = workspaceService.getBusinessData(
                                                LocalDateTime.of(today, LocalTime.MIN),
                                                LocalDateTime.of(today, LocalTime.MAX));
                double turnover = businessData.getTurnover() == null ? 0 : businessData1.getTurnover();
                int validOrderCount = businessData1.getValidOrderCount() == null ? 0 : businessData1.getValidOrderCount();
                double orderCompletionRate = businessData1.getOrderCompletionRate() == null ? 0 : businessData1.getOrderCompletionRate();
                double unitPrice = businessData1.getUnitPrice() == null ? 0 : businessData1.getUnitPrice();
                int newUsers = businessData1.getNewUsers() == null ? 0 : businessData1.getNewUsers();
                row.getCell(1).setCellValue(today.toString());
                row.getCell(2).setCellValue(turnover);
                row.getCell(3).setCellValue(validOrderCount);
                row.getCell(4).setCellValue(orderCompletionRate);
                row.getCell(5).setCellValue(unitPrice);
                row.getCell(6).setCellValue(newUsers);

            }

            //3、通过输出流，将文件传回客户端
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
            out.close();
            excel.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
