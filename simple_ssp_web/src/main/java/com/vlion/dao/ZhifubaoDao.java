package com.vlion.dao;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/22/0022 15:42
 */
public interface ZhifubaoDao {
    // -- SQL01 (查询媒体计划列) 匹配规则字段(计划名称)
    List<List<Object>> QuerySQL1();

    //-- SQL02 曝光,点击,花费,CRT,CPM,CPC ; 匹配规则字段(token)
    List<List<Object>> QuerySQL2(List<String> etlDates, List<String> planIds);

    // -- SQL03 实际单价, 实际消耗 ; 匹配规则字段(token)
    List<List<Object>> QuerySQL3(List<String> etlDates, List<String> adslocationIds);

    // -- SQL4 新客    首拉    mau    首拉/点击    uv/首拉
    List<List<Object>> querySQL4(List<String> etlDates, List<String> planIds);

    //解析输入Excel
    Map<String,List<Object>> parseInputExcel(InputStream is,String fileName) throws IOException;

    Workbook generateOutWorkBook(InputStream is,String fileName,List<List<Object>> datas) throws IOException;

    //
    OutputStream generateExeclOutStream(Map<String,List<List<Object>>> calculatedObject);

}
