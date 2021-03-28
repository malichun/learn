package com.vlion.service;

import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.io.InputStream;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/22/0022 15:42
 */
public interface ZhifubaoService {
    //解析Excel,拼接sql结果
    public Workbook parseExcelService(InputStream is, String fileName) throws IOException;


}
