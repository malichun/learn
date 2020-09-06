package com.vlion.handler

import com.vlion.acc.MapAccumulator
import com.vlion.bean.{CategoryCountInfo, UserVisitAction}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

import scala.collection.mutable

object CategoryTop10Handler {
    def statCategoryTop10(sc: SparkContext, userVisitedActiveRDD: RDD[UserVisitAction]) = {
        //1.注册累加器
        val acc = new MapAccumulator
        sc.register(acc, "CategoryActionAcc")

        //2.遍历日子
        userVisitedActiveRDD.foreach {
            visitAction => {
                if (visitAction.click_category_id != -1) {
                    acc.add((visitAction.click_category_id.toString, "click"))
                } else if (visitAction.order_category_ids != "null") {
                    visitAction.order_category_ids.split(",").foreach {
                        oid => acc.add((oid, "order"))
                    }
                } else if (visitAction.pay_category_ids != "null") {
                    visitAction.pay_category_ids.split(",").foreach {
                        pid => acc.add((pid, "pay"))
                    }
                }
            }
        }

        //3.遍历完成之后就得到每个品类id 和操作类型的数量,然后按照Category进行分组
        val actionCountByCategoryIdMap: Map[String, mutable.Map[(String, String), Long]] = acc.value.groupBy(_._1._1)

        // 4. 转换成 CategoryCountInfo 类型的集合, 方便后续处理
        val categoryCountInfoList: List[CategoryCountInfo] = actionCountByCategoryIdMap.map {
            case (cid, actionMap) => CategoryCountInfo(
                cid,
                actionMap.getOrElse((cid, "click"), 0),
                actionMap.getOrElse((cid, "order"), 0),
                actionMap.getOrElse((cid, "pay"), 0)
            )
        }.toList

        // 5. 按照 点击 下单 支付 的顺序降序来排序
        val sortedCategoryInfoList: List[CategoryCountInfo] = categoryCountInfoList
            .sortBy(info => (info.clickCount, info.orderCount, info.payCount))(Ordering.Tuple3(Ordering.Long.reverse, Ordering.Long.reverse, Ordering.Long.reverse))

        // 6. 截取前 10
        val top10: List[CategoryCountInfo] = sortedCategoryInfoList.take(10)
        // 7. 返回 top10 品类 id
        top10

    }
}

