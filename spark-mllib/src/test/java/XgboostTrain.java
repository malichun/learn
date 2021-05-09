import ml.dmlc.xgboost4j.java.Booster;
import ml.dmlc.xgboost4j.java.DMatrix;
import ml.dmlc.xgboost4j.java.XGBoost;
import ml.dmlc.xgboost4j.java.XGBoostError;

import java.util.HashMap;
import java.util.Map;


/**
 * @description:
 * @author: malichun
 * @time: 2021/4/30/0030 13:37
 */
public class XgboostTrain {
    private static DMatrix trainMat = null;
    private static DMatrix testMat = null;

    public static void main(String [] args) throws XGBoostError {

        try {
            trainMat = new DMatrix("/data/algorithm/test/train.txt");
        } catch (XGBoostError xgBoostError) {
            xgBoostError.printStackTrace();
        }
        try {
            testMat = new DMatrix("/data/algorithm/test/test.txt");
        } catch (XGBoostError xgBoostError) {
            xgBoostError.printStackTrace();
        }

        Map<String, Object> params = new HashMap<String, Object>() {
            {
                put("eta", 1.0);
                put("max_depth", 3);
                put("objective", "binary:logistic");
                put("eval_metric", "logloss");
            }
        };

        Map<String, DMatrix> watches = new HashMap<String, DMatrix>() {
            {
                put("train",trainMat);
                put("test", testMat);
            }
        };
        int nround = 10;
        try {
            Booster booster = XGBoost.train(trainMat, params, nround, watches, null, null);
            booster.saveModel("/data/algorithm/test/model.bin");
        } catch (XGBoostError xgBoostError) {
            xgBoostError.printStackTrace();
        }
    }
}
