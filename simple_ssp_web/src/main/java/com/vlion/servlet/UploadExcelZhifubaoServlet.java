package com.vlion.servlet;

import com.vlion.service.ZhifubaoService;
import com.vlion.service.imp.ZhifubaoServiceImp;
import org.apache.poi.ss.usermodel.Workbook;
import sun.misc.BASE64Encoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/22/0022 14:52
 */
//@WebServlet(name = "/servlet/upload_execel_zhifubao")
@MultipartConfig  //使用MultipartConfig注解标注改servlet能够接受文件上传的请求
public class UploadExcelZhifubaoServlet extends HttpServlet {
    ZhifubaoService zs = new ZhifubaoServiceImp();
    DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyyMMddhhmmss");
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //        设置编码方式
        req.setCharacterEncoding("utf-8");
        resp.setContentType("text/html;charset=utf-8");
        //        设置输出
        Part part = req.getPart("myfile");
        String disposition = part.getHeader("Content-Disposition");  //应该是个名字
        String filename = disposition.substring(disposition.indexOf("filename=\"")+10,disposition.length()-1);
        String suffix = filename.substring(filename.lastIndexOf("."));
        String fileNameHeader = filename.substring(0,filename.lastIndexOf("."));
//        System.out.println(disposition);
//        System.out.println(fileNameHeader);
        //获取上传的文件名
        InputStream is = part.getInputStream();

        String outFileName = fileNameHeader+"_"+getCurrentTime() +suffix;
        String newFilename = encodeFileName(req, outFileName);
        resp.setContentType("text/x-msdownload");
        resp.setHeader("Content-Disposition", "attachment; filename="+newFilename);

        Workbook workbook = zs.parseExcelService(is, filename);
        //输出
        ServletOutputStream out = resp.getOutputStream();
        workbook.write(out);
        out.close();

//        resp.sendRedirect("upload_execel_zhifubao");

    }

    public String getCurrentTime(){
        //格式化
        return formatter3.format(LocalDateTime.now());
    }


    public  String encodeFileName(HttpServletRequest request, String fileName) {
        String name = "";

        String agent = request.getHeader("User-Agent");
        System.out.println(agent);
        try {
            if (agent.contains("Firefox")) {
                BASE64Encoder base64Encoder = new BASE64Encoder();
                name = "=?UTF-8?B?" + new String(base64Encoder.encode(fileName.getBytes("UTF-8"))) + "?=";
            } else {
                name = URLEncoder.encode(fileName, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //System.out.println(name);
        return name;
    }
}
