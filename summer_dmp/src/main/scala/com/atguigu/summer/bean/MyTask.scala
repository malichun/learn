package com.atguigu.summer.bean

/**
 * @description:
 * @author: malichun
 * @time: 2020/9/17/0017 15:24
 *
 * 定义一个任务类,用户创建对象设计逻辑,实现计算功能
 * 序列化从而可以使得对象在网络中传输
 */
class MyTask extends Serializable {
    var data:Int = 0
    var logic: Int => Int = _

    def compute():Int={
        logic(data)
    }
}
