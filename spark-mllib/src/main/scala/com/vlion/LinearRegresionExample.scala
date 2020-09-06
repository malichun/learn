package com.vlion

import org.apache.spark.SparkContext
import org.apache.spark.SparkConf
import org.apache.spark.api.java.JavaRDD
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.{LabeledPoint, LinearRegressionModel, LinearRegressionWithSGD}

object LinearRegresionExample  {
  def main(args: Array[String]): Unit = {


    val conf = new SparkConf().setAppName("JavaLinearRegressionWithSGDExample").setMaster("local")

    val sc = new SparkContext(conf)

    val path = "F:\\Learning\\java\\project\\LearningSpark\\src\\main\\resources\\lpsa.data"


    val data = sc.textFile(path)

    val parsedData = data.map( line  => {

        val parts = line.split(",")
        val features = parts(1).split(" ")
        val v = new Array[Double](features.length)
        for (i <- 0 until features.length - 1) {
          v(i) = features(i).toDouble
        }

        LabeledPoint(parts(0).toDouble, Vectors.dense(v))
    })

    parsedData.cache



    // 模型构建和训练

    val numIterations = 100 // 迭代次数

    val stepSize = 0.00000001 // 学习率

    val model = LinearRegressionWithSGD.train(JavaRDD.toRDD(parsedData), numIterations, stepSize)


    val valueAndPreds = parsedData.map( point => (model.predict(point.features), point.label))

    val MSE = valueAndPreds.map(pair => {

        val diff = pair._1 - pair._2
        diff * diff

    }).mean


    System.out.println("训练数据的均方误差为:" + MSE)





  }


}
