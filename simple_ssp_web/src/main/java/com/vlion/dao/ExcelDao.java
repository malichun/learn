package com.vlion.dao;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/22/0022 15:42
 */
public interface ExcelDao {
    /**
      *  -- SQL01 (查询媒体计划列) 匹配规则字段(计划名称)
      */
    List<List<Object>> QuerySQL1();

    /**
     *-- SQL02 曝光,点击,花费,CRT,CPM,CPC ; 匹配规则字段(token)
     *
     */
    List<List<Object>> QuerySQL2(List<String> etlDates, List<String> planIds);

    /**
     * -- SQL03 实际单价, 实际消耗 ; 匹配规则字段(token)
     *
      */
    List<List<Object>> QuerySQL3(List<String> etlDates, List<String> adslocationIds);

    /**
     * -- SQL4 新客    首拉    mau    首拉/点击    uv/首拉
     * @param etlDates
     * @param planIds
     * @return
     */
    List<List<Object>> querySQL4(List<String> etlDates, List<String> planIds);

    /**
     *
     * 解析输入Excel
     */
    Map<String,List<Object>> parseZfbInputExcel(InputStream is, String fileName) throws IOException;

    Workbook generateZfbOutWorkBook(InputStream is, String fileName, List<List<Object>> datas) throws IOException;

    /**
     * 解析上传的淘宝Excel
     * @param is
     * @param fileName
     * @return
     */
    Map<String, List<String>> parseTbInputExcel(InputStream is, String fileName) throws IOException, ParseException;


    /**
     * 投放计划,包名优化中的ecpm,包名优化-选择日期-ecpm（即最终表格里面的M列成交价）
     * key: plan_id
     * value 结果集
     * @param etlDates
     * @param aduserId 广告主id
     * @return
     */
    List<List<Object>> querySql5(List<String> etlDates,int aduserId);

    /**
     * 数据报告,投放统计,下载表格
     * @param etlDates
     * @param aduserId
     * @return
     */
    List<List<Object>> querySql6(List<String> etlDates,int aduserId);


    /**
     * 数据报告  实时投放统计
     * @param etlDates
     * @param aduserId
     * @return
     */
    List<List<Object>> querySql7(List<String> etlDates,int aduserId);

    Workbook generateTaobaoOutWorkBook(InputStream is, String fileName, List<List<Object>> datas) throws IOException;

}
