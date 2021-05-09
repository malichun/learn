package com.vlion.xgboost_predict;


import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoostError;

public class xgboost_predict_demo {
    public static void main(String[] args) throws XGBoostError {

        long[] rowHeaders = new long[] {0,2,4,7};
        float[] data = new float[] {1f,2f,4f,3f,3f,1f,2f};
        int[] colIndex = new int[] {0,2,0,3,0,1,2};
        int numColumn = 4;
        DMatrix dmat = new DMatrix(rowHeaders, colIndex, data, DMatrix.SparseType.CSR, numColumn);

//
//        long[] colHeaders = new long[] {0,3,4,6,7};
//        float[] data = new float[] {1f,4f,3f,1f,2f,2f,3f};
//        int[] rowIndex = new int[] {0,1,2,2,0,2,1};
//        int numRow = 3;
//        DMatrix dmat = new DMatrix(colHeaders, rowIndex, data, DMatrix.SparseType.CSC, numRow);


    }

}
