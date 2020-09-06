import org.apache.spark.{SparkConf, SparkContext}

object Test {
    def main(args: Array[String]): Unit = {

        val conf = new SparkConf()
            .setAppName("a")
            .setMaster("local[*]")

        val sc = new SparkContext(conf)

        val rdd = sc.textFile("file:///C:/Users/PC//Desktop//test//phone_data .txt")
        rdd
            .map(_.split("\t"))
            .map(fields => (fields(1), (fields(fields.length - 3).toLong, fields(fields.length - 2).toLong)))
            .reduceByKey((t1, t2) => (t1._1 + t2._1, t2._1 + t2._2))
            .collect
            .foreach(t =>
            println(t._1 + "\t" + t._2._2 + "\t" + t._2._1 + "\t" + (t._2._1 + t._2._2))
        )


    }
}
