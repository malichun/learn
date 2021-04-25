package com.vlion.servlet;

import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;

/**
 * @description:
 * @author: malichun
 * @time: 2021/4/20/0020 14:54
 */
public class XgboostPredictServlet {
    public static void main(String[] args) throws XGBoostError {
        float[] data = new float[] {7,23,107,0,0,1711,6413,18,2200};
        int nrow = 1;
        int ncol = 9;
        float missing = 0.0f;
        DMatrix dmat = new DMatrix(data, nrow, ncol);
        Booster booster = XGBoost.loadModel("xgb_model.pkl");
        float[][] predicts = booster.predict(dmat);
        for (float[] array: predicts){
            for (float values: array) {
                System.out.print(values + " ");
            }
            System.out.println();
        }



    }
}
