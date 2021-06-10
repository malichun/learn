package com.atguigu3.chapter03_gui;

import java.util.ArrayList;
import java.util.List;

/**
 * 有一个学生浏览网页的记录程序,它将记录每个学生访问过的网站地址
 * 它由三部分组成:Student,WebPage和StudentTrace三个类
 * -XX:+HeapDumpBeforeFullGc -XX:HeapDumpPath=e:\student.hprof
 */
public class StudentTrace {
    static List<WebPage> webpages = new ArrayList<>();

    public static void createWebPages(){
        for(int i=0;i<100;i++){
            WebPage wp = new WebPage();
            wp.setUrl("http://www."+Integer.toString(i)+".com");
            wp.setConent(Integer.toString(i));
            webpages.add(wp);
        }
    }

    public static void main(String[] args) {
        createWebPages(); //创建了100个网页
        //创建3个学生对象
        Student st3 = new Student(3,"Tom");
        Student st5 = new Student(5,"Jerry");
        Student st7 = new Student(7,"Lily");

        for(int i= 0;i<webpages.size();i++){
            if(i % st3.getId() == 0) {
                st3.visit(webpages.get(i));
            }
            if(i % st5.getId() == 0) {
                st5.visit(webpages.get(i));
            }
            if(i % st7.getId() == 0) {
                st7.visit(webpages.get(i));
            }
        }

        webpages.clear();
        System.gc();

    }

}

//Student浅堆大小 4B(id) + 4B(name) + 4B(history) + 8B(对象头) = 20B -> 填充4B --> 24B
class Student{
    private int id;
    private String name;
    private List<WebPage> hisorty = new ArrayList<>();

    public Student(int id, String name){
        super();
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WebPage> getHisorty() {
        return hisorty;
    }

    public void setHisorty(List<WebPage> hisorty) {
        this.hisorty = hisorty;
    }

    public void visit(WebPage wp ){
        if(wp!=null){
            hisorty.add(wp);
        }
    }

}

class WebPage{
    private String url;
    private String conent;
    public String getUrl(){
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getConent() {
        return conent;
    }

    public void setConent(String conent) {
        this.conent = conent;
    }
}