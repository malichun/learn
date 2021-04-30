package com.vlion.xgboost_predict

import ml.dmlc.xgboost4j.java.{DMatrix, XGBoost}

/**
 * @description:
 * @author: malichun
 * @time: 2021/4/28/0028 17:04
 *
 */
object Xgboost_predict {
    def main(args: Array[String]): Unit = {

        val rowHeaders = Array[Long](0, 2, 4, 7)
        val data = Array[Float](1f, 2f, 4f, 3f, 3f, 1f, 2f)
        val colIndex = Array[Int](0, 2, 0, 3, 0, 1, 2)
        val numColumn = 4
        val dmat = new DMatrix(rowHeaders, colIndex, data, DMatrix.SparseType.CSR, numColumn)


//        val rowHeaders = Array[Long](1,2,3,4,5,6,7,8,9)
//        val data = Array[Float](12,14,332,1,1,684,2835,16,1549)
//        val colIndex = Array[Int](1,2,3,4,5,6,7,8,9)
//        val numColumn = 9
//        val dmat = new DMatrix(rowHeaders, colIndex, data, DMatrix.SparseType.CSR, numColumn)
//
//        val colHeaders = Array[Long](0, 3, 4, 6, 7)
//        val data = Array[Float](1f, 4f, 3f, 1f, 2f, 2f, 3f)
//        val rowIndex = Array[Int](0, 1, 2, 2, 0, 2, 1)
//        val numRow = 3
//        val dmat = new DMatrix(colHeaders, rowIndex, data, DMatrix.SparseType.CSC, numRow)

        //加载python生成的模型
        val model = XGBoost.loadModel("/root/mlc/rtb/jars/xgb_model.pkl_2")


        model.predict(dmat)


    }

}
