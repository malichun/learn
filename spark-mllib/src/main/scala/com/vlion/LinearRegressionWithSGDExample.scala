package com.vlion

import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionWithSGD}
import org.apache.spark.{SparkConf, SparkContext}

object LinearRegressionWithSGDExample {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf().setAppName("LinearRegressionWithSGDExample").setMaster("local[*]")
    val sc = new SparkContext(conf)

    // 加载库自带的数据
    val data = sc.textFile("D:\\360Downloads\\lpsa.data")
    //准备数据，按逗号分隔，按空格分隔
    //变成LabeledPoint格式数据
    val parsedData = data.map { line =>
      val parts = line.split(',')
      LabeledPoint(parts(0).toDouble, Vectors.dense(parts(1).split(' ').map(_.toDouble)))
    }.cache()

    // 设置模型参数调用模型
    val numIterations = 100
    val stepSize = 0.1
    val model = LinearRegressionWithSGD.train(parsedData, numIterations, stepSize)

    // 评估模型：得到预测值
    val valuesAndPreds = parsedData.map { point =>
      val prediction = model.predict(point.features)
      (point.label, prediction)
    }
    //计算均方差
    val MSE = valuesAndPreds.map{ case(v, p) => math.pow((v - p), 2) }.mean()
    println(s"training Mean Squared Error $MSE")


    sc.stop()
  }
}
