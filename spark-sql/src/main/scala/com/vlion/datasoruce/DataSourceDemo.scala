package com.vlion.datasoruce

import org.apache.spark.sql.{DataFrame, Dataset, SaveMode, SparkSession}

object DataSourceDemo {
    def main(args: Array[String]): Unit = {
        val spark:SparkSession = SparkSession.builder().appName("Test")
            .master("local[*]")
            .getOrCreate()

        import spark.implicits._

        val jsonDF:DataFrame = spark.read.json("E:\\gitdir\\learn_projects\\myLearn\\spark-sql\\target\\classes\\user.json")
        val jsonDs:Dataset[User] = jsonDF.as[User]
        jsonDs.foreach(user => println(user.friends(0)))

        jsonDF.write.mode(SaveMode.Overwrite).parquet("E:\\gitdir\\learn_projects\\myLearn\\spark-sql\\target\\classes\\user.parquet")

        val parDF = spark.read.parquet("E:\\gitdir\\learn_projects\\myLearn\\spark-sql\\target\\classes\\user.parquet")
        val userDS = parDF.as[User]
        userDS.map(user => {
            user.name ="zl"
            user.friends(0)="志玲"
            user
        }).show()


    }
}

case class User(var name:String,age:Long,friends:Array[String])
