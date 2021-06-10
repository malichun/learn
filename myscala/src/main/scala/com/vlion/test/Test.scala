package com.vlion.test;

import scala.io.Source
import scala.collection.mutable

object Test{
    def main(args: Array[String]): Unit = {
        val set=Set("26532","25097","24825","24780","24793","26742","26665","27084","26357","26590","26553","26662","27110")
        val map = (mutable.Map[String, Int]() /: set) ((m, e) => {
            m.put(e, 0)
            m
        })
        val source = Source.fromFile("train.txt")
        val adIds = source.getLines().toList.collect{ case s if s != null && s != "" => s.split("#")(7)}

        adIds.foldLeft(map)((m,adid) => {
            m.update( adid,m.getOrElse( adid,0) + 1)
            m
        })

        source.close()



    }
}