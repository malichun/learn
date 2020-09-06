package com.vlion.handler

import com.vlion.bean.{CategoryCountInfo, CategorySession, UserVisitAction}
import org.apache.spark.{Partitioner, SparkContext}
import org.apache.spark.rdd.RDD

import scala.collection.mutable

object CategorySessionHandler {
    def statCategoryTop10Session(sc: SparkContext,
                                 userVisitActionRDD: RDD[UserVisitAction],
                                 categoryTop10: List[CategoryCountInfo]) = {

        //1.得到top10的品类id
        val categoryIdTop10: List[String] = categoryTop10.map(_.categoryId)
        //2.过去出来只包含top10 品类id的那些用户行为
        val filteredUserVisitActionRDD = userVisitActionRDD.filter(userVisitAction => {
            categoryIdTop10.contains(userVisitAction.click_category_id.toString)
        })

        //3.聚合操作
        // => RDD[(品类id,sessionId)] map
        // => RDD[(品类id,sessionId),1]
        val categorySessionOne = filteredUserVisitActionRDD.map(userVisitAction => ((userVisitAction.click_category_id, userVisitAction.session_id), 1))

        val categorySessionCount = categorySessionOne.reduceByKey(_ + _).map { case ((cid, sid), count) => (cid, (sid, count)) }

        //4.按照品类id进行分组
        val categorySessionCountGrouped = categorySessionCount.groupByKey()


        val categorySessionRDD = categorySessionCountGrouped.flatMap {
            case (cid, it) =>
                val list = it.toList.sortBy(_._2)(Ordering.Int.reverse).take(10)
                val result = list.map { case (sid, count) => CategorySession(cid.toString, sid, count) }
                result
        }

        categorySessionRDD.collect().foreach(println)
    }

    /*
1. 得到top10的品类的id

2. 过去出来只包含 top10 品类id的那些用户行为

3. 分组计算
    => RDD[(品类id, sessionId))] map
    => RDD[(品类id, sessionId), 1)]  reduceByKey
    => RDD[(品类id, sessionId), count)]    map
    => RDD[品类id, (sessionId, count)]     groupByKey
    RDD[品类id, Iterator[(sessionId, count)]]
 */


    /**
     * 因为it.toList.sort(_._2)(Ordering.Int.reverse) 全部在内存中,可能会有oom异常
     *
     * 解决方案1:
     * 使用 RDD 的排序功能, 但是由于 RDD 排序是对所有的数据整体排序, 所以一次只能针对一个 CategoryId 进行排序操作.
     *
     * @param sc
     * @param userVisitActionRDD
     * @param categoryTop10
     */
    def statCategoryTop10Session_1(sc: SparkContext,
                                   userVisitActionRDD: RDD[UserVisitAction],
                                   categoryTop10: List[CategoryCountInfo]) = {
        //1.得到top10的品类id
        val categoryIdTop10 = categoryTop10.map(_.categoryId)

        val broadcastVar = sc.broadcast(categoryIdTop10)
        //2.过滤出来只包含top10品类id的那些行为
        val filteredUserVisitActionRDD = userVisitActionRDD
            .filter(uservistiAction => broadcastVar.value.contains(uservistiAction.click_category_id.toString))

        //聚合操作
        // => RDD[(品类id,sessionId)] map
        //  => RDD[(品类id,sessionId)],1
        val categorySessionCount = filteredUserVisitActionRDD
            .map(userVisitAction => ((userVisitAction.click_category_id, userVisitAction.session_id), 1))
            .reduceByKey(_ + _)
            .map { case ((cid, sid), count) => (cid, (sid, count)) }

        //每个品类id排序取10的session
        categoryIdTop10.foreach(cid => {
            //针对某个具体的categoryId,过滤出来的只包含这个CategoryId的RDD,然后整体降序排列
            val top10 = categorySessionCount.filter(_._1 == cid.toLong)
                .sortBy(_._2._2, ascending = false)
                .take(10)
                .map {
                    case (cid, (sid, count)) => CategorySession(cid.toString, sid, count)
                }

            top10.foreach(println)
        })


    }


    // TODO 方案3,可以把同一个品类的数据都进入到同一个分区内,然后对每个分区的数据进行排序
    // TODO 需要用到自定义分区器
    def statCategoryTop10Session_2(sc: SparkContext, userVisitActionRDD: RDD[UserVisitAction], categoryTop10: List[CategoryCountInfo]) = {

        // 1. 得到top10的品类的id
        val categoryIdTop10 = categoryTop10.map(_.categoryId)
        // 2. 过去出来只包含 top10 品类id的那些用户行为
        val filteredUserVisitActionRDD=userVisitActionRDD.filter(UserVisitAction => {
                categoryIdTop10.contains(UserVisitAction.click_category_id.toString)
            })

        //3.聚合操作
        val categorySessionOne: RDD[((Long, String), Int)] = filteredUserVisitActionRDD
            .map(userVisitAction => ((userVisitAction.click_category_id, userVisitAction.session_id), 1))

        val categorySessionCount=  categorySessionOne.reduceByKey(new MyPartitioner(categoryIdTop10),_+_)
            .map{
                case ((cid,sid),count) => CategorySession(cid.toString, sid, count)
            }

        categorySessionCount.mapPartitions(it => {
            // 这个时候也不要把 it 变化 list 之后再排序, 否则仍然会有可能出现内存溢出.
            // 我们可以把数据存储到能够自动排序的集合中 比如 TreeSet 或者 TreeMap 中, 并且永远保持这个集合的长度为 10
            // 让TreeSet默认安装 count 的降序排列, 需要让CategorySession现在 Ordered 接口(Comparator)
            var top10: mutable.TreeSet[CategorySession] = mutable.TreeSet[CategorySession]()
            it.foreach(cs => {
                top10 += cs // 把 CategorySession 添加到 TreeSet 中
                if (top10.size > 10) { // 如果 TreeSet 的长度超过 10, 则移除最后一个
                    top10 = top10.take(10)
                }
            })
            top10.toIterator
        })

    }

}

class MyPartitioner(categoryIdTop10: List[String]) extends Partitioner {
    //给每一个cid配一个分区号(使用他们的索引就行了)
    private val cidAndIndex = categoryIdTop10.zipWithIndex.toMap

    override def numPartitions: Int = categoryIdTop10.size

    override def getPartition(key: Any): Int = {
        key match {
            case (cid: Long, _) => cidAndIndex(cid.toString)
        }
    }
}
