package com.vlion;

import org.apache.hadoop.hive.ql.exec.UDF;

/**
 * @description:
 * @author: malichun
 * @time: 2020/11/9/0009 15:40
 */
public class NumberUDF extends UDF {
    public String evaluate (final Object s) {
        return ChhUtil.strAddComma(s.toString());
    }
}
