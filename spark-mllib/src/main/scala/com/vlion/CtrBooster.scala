package com.vlion

/**
 * @description:
 * @author: malichun
 * @time: 2021/4/23/0023 10:32
 *
 */
import ml.dmlc.xgboost4j.scala.spark.XGBoost
import org.apache.spark.ml.evaluation.{BinaryClassificationEvaluator, RegressionEvaluator}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.ml.feature.LabeledPoint
import org.apache.spark.ml.linalg.DenseVector
import org.apache.spark.sql.{Row, SparkSession}
import org.apache.spark.sql.types.{DoubleType, StructField, StructType}
import org.apache.spark.ml.linalg.SQLDataTypes.VectorType

/**
 * User: leonb
 * Date: 2/11/18
 * Time: 8:21 PM
 */
object CtrBooster extends App {
    println("lets boost")

    val conf = new SparkConf()
        .setAppName("ctrBooster")
        .setMaster("local[*]")

    val spark = SparkSession
        .builder()
        .config(conf)
        .getOrCreate()

    val sc = spark.sparkContext

    val trainRDD = sc.parallelize(Seq(
        LabeledPoint(1.0, new DenseVector(Array(2.0, 3.0, 4.0))),
        LabeledPoint(0.0, new DenseVector(Array(5.0, 5.0, 5.0))),
        LabeledPoint(1.0, new DenseVector(Array(2.0, 3.0, 4.0))),
        LabeledPoint(0.0, new DenseVector(Array(5.0, 5.0, 5.0))),
        LabeledPoint(1.0, new DenseVector(Array(2.0, 3.0, 4.0))),
        LabeledPoint(0.0, new DenseVector(Array(5.0, 5.0, 5.0))),
        LabeledPoint(1.0, new DenseVector(Array(2.0, 3.0, 4.0))),
        LabeledPoint(0.0, new DenseVector(Array(5.0, 5.0, 5.0))),
        LabeledPoint(1.0, new DenseVector(Array(2.0, 3.0, 4.0))),
        LabeledPoint(0.0, new DenseVector(Array(5.0, 5.0, 5.0))),
        LabeledPoint(1.0, new DenseVector(Array(2.0, 3.0, 4.0))),
        LabeledPoint(1.0, new DenseVector(Array(2.0, 3.0, 4.0))),
        LabeledPoint(0.0, new DenseVector(Array(5.0, 5.0, 5.0)))
    ), 4)

    val trainRDDofRows = trainRDD.map { labeledPoint => Row(labeledPoint.label, labeledPoint.features) }

    val trainSchema = StructType(
        Array(
            StructField("label", DoubleType),
            StructField("features", VectorType)
        ))

    val trainDF = spark.createDataFrame(trainRDDofRows, trainSchema)

    val paramMap = List(
        "eta" -> 0.1f,
        "max_depth" -> 2,
        "objective" -> "binary:logistic").toMap


    XGBoost()
    val xgboostModelDF = XGBoost.trainWithDataFrame(
        trainingData = trainDF,
        params = paramMap,
        round = 1,
        nWorkers = 4,
        useExternalMemory = true
    )


    val predictions = xgboostModelDF.transform(trainDF)

    predictions.show()
    val evaluator = new BinaryClassificationEvaluator()
        .setLabelCol("label")
        .setRawPredictionCol("prediction")
        .setMetricName("areaUnderROC")

    val auroc = evaluator.evaluate(predictions)
    println(s"auroc = $auroc")

}
