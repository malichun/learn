package com.atguigu.hive.udtf;

import org.apache.hadoop.hive.ql.exec.UDFArgumentException;
import org.apache.hadoop.hive.ql.metadata.HiveException;
import org.apache.hadoop.hive.ql.udf.generic.GenericUDTF;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.ObjectInspectorFactory;
import org.apache.hadoop.hive.serde2.objectinspector.StructObjectInspector;
import org.apache.hadoop.hive.serde2.objectinspector.primitive.PrimitiveObjectInspectorFactory;
import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @description:
 * @author: malichun
 * @time: 2020/11/2/0002 18:19
 */
public class ExplodeJSONArray extends GenericUDTF {

    @Override
    public StructObjectInspector initialize(StructObjectInspector argOIs) throws UDFArgumentException {
        //1参数合法性检查
        if(argOIs.getAllStructFieldRefs().size()!=1){
            throw new UDFArgumentException("ExplodeJSONArray 只需要一个参数");
        }

        //2第一个参数必须为string
        if(!"string".equals(argOIs.getAllStructFieldRefs().get(0).getFieldObjectInspector().getTypeName())){
            throw new UDFArgumentException("json_array_to_struct_array的第1个参数应为string类型");
        }

        //3.定义返回值名称和类型
        List<String> fieldNames = new ArrayList<>();
        List<ObjectInspector> fieldOIs = new ArrayList<>();

        fieldNames.add("items");
        fieldOIs.add(PrimitiveObjectInspectorFactory.javaStringObjectInspector);

        return ObjectInspectorFactory.getStandardStructObjectInspector(fieldNames,fieldOIs);

    }

    @Override
    public void process(Object[] objects) throws HiveException {
        //1.获取传入的数据
        String jsonArray = objects[0].toString();

        //2.将String转换为json数组
        JSONArray actions = new JSONArray(jsonArray);

        //3.循环一次,去除数组中的一个json,并写出
        for(int i = 0 ;i< actions.length();i++){
            String[] result = new String[1];
            result[0] = actions.getString(i);
            forward(result); //返回一个数组
        }

    }

    @Override
    public void close() throws HiveException {

    }
}
