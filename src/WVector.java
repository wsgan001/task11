import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by liuyuan on 4/3/16.
 */
public class WVector {
//    private double w0 = 0.5;
//    private double w1 = 0.5;
//    private double w2 = 0.5;
//    private double w3 = 0.5;
//    private double w4 = 0.5;
//    private double w5 = 0.5;
    private static double[] w = {0.5, 0.5, 0.5, 0.5, 0.5, 0.5};
    private static List<ProdSelection> train;
    private static List<ProdSelection> test;

    public static void main(String[] args) {
        KNN knn = new KNN();
        for (int i = 0; i < 6; i++) {
            WeightAndAccuracy optimal = null;
            for (double j = 0; j < 1; j += 0.001) {
                w[i] = j;
                Result result = knn.predict(train, test, w);
                if (optimal == null) {
                    optimal = new WeightAndAccuracy(j, result.accuracy);
                } else {
                    if (result.accuracy > optimal.accuracy) {
                        optimal = new WeightAndAccuracy(j, result.accuracy);
                    }
                }
                System.out.println("weight = " + j + "\n accuracy = " + result.accuracy);
            }
            w[i] = optimal.weight;
        }
    }

    static class WeightAndAccuracy {
        private double weight;
        private double accuracy;

        public WeightAndAccuracy (double weight, double accuracy) {
            this.weight = weight;
            this.accuracy = accuracy;
        }
    }
}
