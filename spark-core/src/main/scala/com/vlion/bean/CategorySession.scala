package com.vlion.bean

case class CategorySession(categoryId:String,
                           sessionId:String,
                           clickCount:Long
                          ) extends Ordered[CategorySession]{
    override def compare(that: CategorySession): Int = {
        if(this.categoryId <= that.categoryId) 1
        else -1
    }
}
