package com.htsec.bigdata;


import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @description:
 * @author: malichun
 * @time: 2020/11/9/0009 16:06
 */
public class NumUDF extends UDF {
    public String evaluate (final String s) {
        return ChhUtil.strAddComma(s);
    }
}
