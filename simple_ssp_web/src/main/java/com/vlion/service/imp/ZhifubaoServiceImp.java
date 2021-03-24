package com.vlion.service.imp;

import com.vlion.dao.ZhifubaoDao;
import com.vlion.dao.imp.ZhifubaoDaoImp;
import com.vlion.service.ZhifubaoService;
import com.vlion.utils.Utils;
import org.apache.poi.ss.usermodel.Workbook;
import org.javatuples.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @description:
 * @author: malichun
 * @time: 2021/3/22/0022 15:47
 */
public class ZhifubaoServiceImp implements ZhifubaoService {
    ZhifubaoDao zd = new ZhifubaoDaoImp();

    @Override
    public Workbook parseExcelService(InputStream is,String fileName) throws IOException {


        Map<String, List<Object>> excelListMap = zd.parseInputExcel(is,fileName); //key是 date_channel

        List<String> etlDateList = excelListMap.remove("etlDateSet").stream().map(Object::toString).collect(Collectors.toList()); //拿到处理时间

        List<List<Object>> res1 = zd.QuerySQL1(); //所有的计划id,
        Map<String,Map<String,List<Object>>> map = getAllPlanNameIDMap(res1);
        Map<String,List<Object>> allPlanNameIDMap = map.get("allPlanNameID"); // 计划name, list
        Map<String,List<Object>> allPlanIDAdslocateIDMap = map.get("allPlanIDAdslocateID");  //广告位id, list

        //查找出所有的匹配到的计划plan_id //找出广告位id,为sql3准备
        List<Pair<String,Object>> innerPlanIdAdsolcateId = excelListMap.entrySet().stream().map(m -> {
            String key = m.getKey();// 日期_channel
            List<Object> value = m.getValue();  //整个ArrayList
            List<Object> query1Res = allPlanNameIDMap.get(key.substring(key.indexOf("_")+1));
            if(query1Res != null && query1Res.get(0) != null){
                return Pair.with(query1Res.get(0).toString(),query1Res.get(3)); //返回一个元组(plainid,adslotId)
            }
            return null;
        }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        //找出匹配的计划id
        List<String> innerPlanIds = innerPlanIdAdsolcateId.stream()
                .map(Pair::getValue0)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());

        //查找sql2,计划id
        List<List<Object>> query2Res = zd.QuerySQL2(etlDateList,innerPlanIds);
        //找出匹配的广告位id
        List<String> innerAdsolcateId = innerPlanIdAdsolcateId
                .stream()
                .map(pair -> {
                    if (pair.getValue1() != null) {
                        return pair.getValue1().toString();
                    }
                    return null;
                }).filter(Objects::nonNull).distinct().collect(Collectors.toList());

        //查找广告位id
        List<List<Object>> query3Res = zd.QuerySQL3(etlDateList, innerAdsolcateId);

        //query2,query3的map
        Map<String, List<Object>> planId2Map = getTokenMap(query2Res);
        Map<String, List<Object>> adsoltId3Map = getTokenMap(query3Res);

        //开始组装
       List<List<Object>> generatedList =  excelListMap.entrySet().stream().map(e -> {
            String key = e.getKey();  //date_channel
            List<Object> value = e.getValue(); // 一行所有
            String[] arr = key.split("_");
            String date1 = arr[0];
            String col0 = Utils.convertDateStr(date1); //日期
            String col1 = arr[1]; //名称
            List<Object> planNameObjects = allPlanNameIDMap.get(col1);
            if(planNameObjects ==null){
                return null;
            }

            Object col2 = planNameObjects.get(2); //媒体
            String planId =Utils.getObjectValueString(planNameObjects.get(0));

            //sql的key
            String datePlanIdKey = col0+"_"+planId;

           String col3 =  planId + " / " + Utils.getObjectValueString(planNameObjects.get(1)); // 计划
           List<Object> planIdValueList = planId2Map.get(datePlanIdKey);

           int col4=0;
           int col5 = 0;
           double col6 = 0.0;
           double col7 =0.0;
           double col8 =0.0;
           double col9 =0.0;
           if(planIdValueList != null){
               col4 = Utils.getObjectValueInteger(planIdValueList.get(3)); // 曝光
               col5 = Utils.getObjectValueInteger(planIdValueList.get(4)); // 点击
               col6 = Utils.getObjectValueDouble(planIdValueList.get(5)); // 花费
               col7 = Utils.getObjectValueDouble(planIdValueList.get(6)); // ctr
               col8 = Utils.getObjectValueDouble(planIdValueList.get(7)); // cpm
               col9 = Utils.getObjectValueDouble(planIdValueList.get(8)); // cpc
           }


            //拼接sql3的数据,广告位id
            String dateAdslotIdKey = col0 +"_"+ planNameObjects.get(3);

            List<Object> listAdsoltId = adsoltId3Map.get(dateAdslotIdKey);
            double col10 = 0.0;
            if(listAdsoltId != null){
                col10 = Utils.getObjectValueDouble(listAdsoltId.get(2)); //实际单价
            }

           double col11 = 0.0;
           double col13 = 0.0;
           double col14 = 0.0;
           double col15 = 0.0;

            double col12 = Utils.getObjectValueDouble(value.get(9)); //返佣金额

           if(planIdValueList != null){
//             col11 = new BigDecimal(col10).multiply(new BigDecimal(col4)).divide(new BigDecimal(1000),3).doubleValue();//实际消耗 (实际单价 * 曝光 / 1000)
               col11 =col10 * col4 / 1000;
                col13 = col12 - col11; //盈亏 (返佣金额 - 实际消耗)
//                col14 = new BigDecimal(col12).divide(new BigDecimal(col4),3).multiply(new BigDecimal(1000)).doubleValue();// ECPM  (返佣金额 / 曝光 * 1000 )
               col14 = col12 / col4 * 1000;
//                col15 = new BigDecimal(col12).divide(new BigDecimal(col5),3).doubleValue();// ecpc  返佣金额 / 点击
               col15 = col12 / col5;// ecpc  返佣金额 / 点击
           }



            //uv个数 = 当日有效拉新用户数 + 30日内有效召回用户数 + 30日有效召回用户数 + 60日有效召回用户数 + 90日有效召回用户数
            int col16 = Utils.getObjectValueInteger(value.get(3))
                    + Utils.getObjectValueInteger(value.get(7))
                    + Utils.getObjectValueInteger(value.get(13))
                    + Utils.getObjectValueInteger(value.get(15));
            //剩下的3-29

            List<Object> return_ = new ArrayList<>();
            return_.add(col0);
            return_.add(col1);
            return_.add(col2);
            return_.add(col3);
            return_.add(col4);
            return_.add(col5);
            return_.add(col6);
            return_.add(col7);
            return_.add(col8);
            return_.add(col9);
            return_.add(col10);
            return_.add(col11);
            return_.add(col12);
            return_.add(col13);
            return_.add(col14);
            return_.add(col15);
            return_.add(col16);
            return_.addAll(value.subList(3,value.size()));

            return return_;
        }).filter(Objects::nonNull)
               .collect(Collectors.toList());

        Workbook workbook = zd.generateOutWorkBook(getTemplateInputStream(), "支付宝报告模版.xls", generatedList);

        return workbook;
    }

    private InputStream getTemplateInputStream(){
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("支付宝报告模版.xls");
        return in;
    }



    //获取planid
    private Map<String,Map<String,List<Object>>> getAllPlanNameIDMap(List<List<Object>> list){
        Map<String,List<Object>> allPlanNameIDMap = new HashMap<>();
        Map<String,List<Object>> allPlanIDAdslocateIDMap = new HashMap<>();
        Map<String,Map<String,List<Object>>> _return = new HashMap<String,Map<String,List<Object>>>(){{
            put("allPlanNameID",allPlanNameIDMap);
            put("allPlanIDAdslocateID",allPlanIDAdslocateIDMap);
        }};

        for(List<Object> list1 : list){
            Object fullName = list1.get(1);

            if(fullName != null && !fullName.toString().equals("")){
                String[] arr = fullName.toString().split("-");
                if(arr.length != 1){
                    allPlanNameIDMap.put(arr[0],list1); //计划Name的Map
                    if(list1.get(3) != null ){
                        allPlanIDAdslocateIDMap.put(list1.get(3).toString(),list1); //广告位id的Map
                    }
                }
            }
        }
        return _return;
    }

    //sql2的结果转换为map,sql3的结果转map  (token,list<Object>)
    private Map<String,List<Object>> getTokenMap(List<List<Object>> queryRes2){
        Map<String,List<Object>> listMap = new HashMap<>();
        queryRes2.forEach(e -> {
            listMap.put(e.get(0).toString(),e);
        });
        return listMap;
    }

}
