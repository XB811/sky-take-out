package com.sky.service;

import com.sky.vo.OrderReportVO;
import com.sky.vo.SalesTop10ReportVO;
import com.sky.vo.TurnoverReportVO;
import com.sky.vo.UserReportVO;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;

/**
 * @FileName ReportService
 * @Description
 * @Author xb
 * @date 2024-09-15
 **/

public interface ReportService {
    /**
    * 统计指定时间区间内的营业额
    * @param begin 
     * @param end 
    * @return TurnoverReportVO 
    * @Date 2024/9/15 00:40
    */
    TurnoverReportVO getTurnoverStatistics(LocalDate begin, LocalDate end);

    UserReportVO getUserStatistics(LocalDate begin, LocalDate end);

    OrderReportVO getOrdersStatistics(LocalDate begin, LocalDate end);

    /**
    * 统计指定时间内的效率前10
    * @param begin 
     * @param end 
    * @return SalesTop10ReportVO 
    * @Date 2024/9/15 01:57
    */
    SalesTop10ReportVO getSalesTop10(LocalDate begin, LocalDate end);

    /**
    * 导出Excel报表接口
    * @param response
    * @return
    * @Date 2024/9/15 03:31
    */
    void exportBusinessData(HttpServletResponse response);
}
