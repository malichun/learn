package com.vlion.ss.baseoperation

import org.apache.spark.sql.{DataFrame, SparkSession}
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}


object BaseOperation3 {
    def main(args: Array[String]): Unit = {
        val spark: SparkSession = SparkSession
            .builder()
            .master("local[*]")
            .appName("BaseOperation3")
            .getOrCreate()

        import spark.implicits._

        val peopleSchema = StructType(
            StructField("name", StringType) ::
                StructField("age", LongType) ::
                StructField("sex", StringType)
                :: Nil
        )

        val peopleDF:DataFrame = spark.readStream
            .schema(peopleSchema)
            .json("C:\\Users\\PC\\Desktop\\data")

        peopleDF.createOrReplaceTempView("people") //创建临时表
        val df:DataFrame = spark.sql("select * from people where age>20")

        df.writeStream
            .outputMode("append")
            .format("console")
            .start
            .awaitTermination()

    }
}
