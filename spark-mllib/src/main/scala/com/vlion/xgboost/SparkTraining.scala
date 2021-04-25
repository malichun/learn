package com.vlion.xgboost

/**
 * @description:
 * @author: malichun
 * @time: 2021/4/23/0023 10:41
 *
 */
import ml.dmlc.xgboost4j.scala.spark.XGBoostClassifier
import org.apache.spark.ml.feature.{StringIndexer, VectorAssembler}
import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{DoubleType, StringType, StructField, StructType}

import scala.collection.mutable.ArrayBuffer

/**
 * xgboost算法演示
 */
// this example works with Iris dataset (https://archive.ics.uci.edu/ml/datasets/iris)
object SparkTraining {

    def main(args: Array[String]): Unit = {
        val spark = SparkSession.builder()
            .appName(this.getClass.getSimpleName)
            .master("local[5]")
            .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
            .getOrCreate()

        val inputPath = "data/xgboost/sample.csv"

        //定义数据的schema
        val schema = new StructType(Array(
            StructField("sepal length", DoubleType, true),
            StructField("sepal width", DoubleType, true),
            StructField("petal length", DoubleType, true),
            StructField("petal width", DoubleType, true),
            StructField("class", StringType, true)))

        //读取数据集
        val rawInput = spark.read.schema(schema).csv(inputPath)

        /**
         * +------------+-----------+------------+-----------+-----------+
         * |sepal length|sepal width|petal length|petal width|class      |
         * +------------+-----------+------------+-----------+-----------+
         * |5.1         |3.5        |1.4         |0.2        |Iris-setosa|
         * |4.9         |3.0        |1.4         |0.2        |Iris-setosa|
         * |4.7         |3.2        |1.3         |0.2        |Iris-setosa|
         * |4.6         |3.1        |1.5         |0.2        |Iris-setosa|
         * |5.0         |3.6        |1.4         |0.2        |Iris-setosa|
         * |5.4         |3.9        |1.7         |0.4        |Iris-setosa|
         * |4.6         |3.4        |1.4         |0.3        |Iris-setosa|
         * |5.0         |3.4        |1.5         |0.2        |Iris-setosa|
         * |4.4         |2.9        |1.4         |0.2        |Iris-setosa|
         * |4.9         |3.1        |1.5         |0.1        |Iris-setosa|
         * +------------+-----------+------------+-----------+-----------+
         */
        //    rawInput.show(10,false)
        // transform class to index to make xgboost happy
        val stringIndexer = new StringIndexer()
            .setInputCol("class")
            .setOutputCol("classIndex")
            .fit(rawInput)

        val labelTransformed = stringIndexer.transform(rawInput).drop("class")

        /**
         * +------------+-----------+------------+-----------+----------+
         * |sepal length|sepal width|petal length|petal width|classIndex|
         * +------------+-----------+------------+-----------+----------+
         * |5.1         |3.5        |1.4         |0.2        |0.0       |
         * |4.9         |3.0        |1.4         |0.2        |0.0       |
         * |4.7         |3.2        |1.3         |0.2        |0.0       |
         * |4.6         |3.1        |1.5         |0.2        |0.0       |
         * |5.0         |3.6        |1.4         |0.2        |1.0       |
         * |5.4         |3.9        |1.7         |0.4        |1.0       |
         * |4.6         |3.4        |1.4         |0.3        |2.0       |
         * |5.0         |3.4        |1.5         |0.2        |2.0       |
         * |4.4         |2.9        |1.4         |0.2        |2.0       |
         * |4.9         |3.1        |1.5         |0.1        |2.0       |
         * +------------+-----------+------------+-----------+----------+
         */
        labelTransformed.show(10, false)

        // 将所有特征列转成向量
        val vectorAssembler = new VectorAssembler()
            //.setInputCols(Array("sepal length", "sepal width", "petal length", "petal width")).
            .setInputCols(getColumnArray(labelTransformed))
            .setOutputCol("features")

        val xgbInput = vectorAssembler.transform(labelTransformed).select("features", "classIndex")

        /**
         * +-----------------+----------+
         * |features         |classIndex|
         * +-----------------+----------+
         * |[5.1,3.5,1.4,0.2]|0.0       |
         * |[4.9,3.0,1.4,0.2]|0.0       |
         * |[4.7,3.2,1.3,0.2]|0.0       |
         * |[4.6,3.1,1.5,0.2]|0.0       |
         * |[5.0,3.6,1.4,0.2]|1.0       |
         * |[5.4,3.9,1.7,0.4]|1.0       |
         * |[4.6,3.4,1.4,0.3]|2.0       |
         * |[5.0,3.4,1.5,0.2]|2.0       |
         * |[4.4,2.9,1.4,0.2]|2.0       |
         * |[4.9,3.1,1.5,0.1]|2.0       |
         * +-----------------+----------+
         */
        xgbInput.show(10, false)

        //训练集，预测集
        val Array(train, test) = xgbInput.randomSplit(Array(0.9, 0.1))

        // 注意!!!这个num_workers 必须小于等于 local[5] 线程数,否则会出现程序卡死现象.
        val xgbParam = Map("eta" -> 0.1f,
            "max_depth" -> 2,
            "objective" -> "multi:softprob",
            "num_class" -> 3,
            "num_round" -> 100,
            "num_workers" -> 5)

        // 创建xgboost函数,指定特征向量和标签
        val xgbClassifier = new XGBoostClassifier(xgbParam)
            .setFeaturesCol("features")
            .setLabelCol("classIndex")

        //开始训练
        val xgbClassificationModel = xgbClassifier.fit(train)

        //预测
        val results = xgbClassificationModel.transform(test)

        /**
         * +--------------------+----------+--------------------+--------------------+----------+
         * |            features|classIndex|       rawPrediction|         probability|prediction|
         * +--------------------+----------+--------------------+--------------------+----------+
         * |[4.6,3.1,1.5,0.2,...|       0.0|[3.43588137626647...|[0.98977124691009...|       0.0|
         * |[4.8,3.4,1.6,0.2,...|       0.0|[3.43588137626647...|[0.98977124691009...|       0.0|
         * |[5.0,2.3,3.3,1.0,...|       1.0|[-1.9347994327545...|[0.00610134331509...|       1.0|
         * |[5.0,3.2,1.2,0.2,...|       0.0|[3.43588137626647...|[0.98977124691009...|       0.0|
         * |[5.5,2.4,3.8,1.1,...|       1.0|[-1.9347994327545...|[0.00610134331509...|       1.0|
         * |[5.7,2.9,4.2,1.3,...|       1.0|[-1.9347994327545...|[0.00610134331509...|       1.0|
         * |[5.8,2.6,4.0,1.2,...|       1.0|[-1.9347994327545...|[0.00556284701451...|       1.0|
         * |[5.8,2.7,5.1,1.9,...|       2.0|[-1.9347994327545...|[0.00450986577197...|       2.0|
         * |[6.0,3.4,4.5,1.6,...|       1.0|[-1.9347994327545...|[0.00870351772755...|       1.0|
         * |[6.1,2.6,5.6,1.4,...|       2.0|[-1.9347994327545...|[0.00494972383603...|       2.0|
         * |[6.1,2.8,4.7,1.2,...|       1.0|[-1.9347994327545...|[0.00601264182478...|       1.0|
         * |[6.4,3.1,5.5,1.8,...|       2.0|[-1.9347994327545...|[0.00451971078291...|       2.0|
         * |[6.4,3.2,5.3,2.3,...|       2.0|[-1.9347994327545...|[0.00451971078291...|       2.0|
         * |[6.7,2.5,5.8,1.8,...|       2.0|[-1.9347994327545...|[0.00450955657288...|       2.0|
         * |[6.7,3.0,5.0,1.7,...|       1.0|[-1.9347994327545...|[0.00869614630937...|       1.0|
         * |[6.9,3.1,4.9,1.5,...|       1.0|[-1.9347994327545...|[0.00830076728016...|       1.0|
         * |[7.2,3.0,5.8,1.6,...|       2.0|[-1.9347994327545...|[0.00496413139626...|       2.0|
         * +--------------------+----------+--------------------+--------------------+----------+
         */
        results.show()

        spark.close()
    }

    /**
     * 获取所有列转为Array数组
     *
     * @param df
     * @return
     */
    def getColumnArray(df: DataFrame): Array[String] = {
        var columns: Array[String] = df.columns.clone()
        //drop column : classIndex
        columns = columns.dropRight(1)
        val featuresColumns = new ArrayBuffer[String]()
        for (column <- columns) {
            featuresColumns += column
        }
        featuresColumns.toArray
    }
}
