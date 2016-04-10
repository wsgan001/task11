import java.util.*;

/**
 * Created by liuyuan on 4/3/16.
 */
public class WVector {
    private static double[] w = {1, 1, 1, 1, 1, 1};
    private static final int DROP = 10;

    public static void main(String[] args) {

        double[] wOld  = new double[6];

        System.arraycopy(w, 0, wOld, 0, 6);

        while (true) {

            double[] bestW = new double[6];
            double bestAccuracy = 0;

            for (int i = 0; i < 5; i++) {
                double[] wTemp = optimize(w);
                double accuracy = validate(wTemp, wOld);
                if (accuracy != 0) {
                    if (accuracy > bestAccuracy) {
                        bestAccuracy = accuracy;
                        bestW = wTemp;
                    }
                }
            }

            if (bestAccuracy != 0) {
                w = bestW;
                System.arraycopy(w, 0, wOld, 0, 6);
            } else {
                break;
            }
        }
    }
    // 0.0116, 0, 0.0349, 1, 0.0756, 0.6337

    public static boolean hasFinished (ArrayList<Double> list) {
        if (list.size() < DROP ) {
            return false;
        }
        for (int i = 0; i < DROP - 1; i++) {
            if (list.get(i + 1) > list.get(i)) {
                return false;
            }
        }
        return true;
    }

    private static double validate (double[] wNew, double[] wOld) {
        int winCount = 0;
        double accuracy = 0;
        for (int i = 0; i < 100; i++) {
            KNN_combined knn = new KNN_combined(ProdSelection.class, 3);
            double scoreNew = knn.crossValidation(wNew);
            accuracy += scoreNew;
            // System.out.println("New score: " + scoreNew);
            double scoreOld = knn.crossValidation(wOld);
            // System.out.println("Old score: " + scoreOld);
            if (scoreNew > scoreOld) {
                winCount++;
            }
        }
        accuracy = accuracy / 100;
        if (winCount >= 55) {
            System.out.println(winCount);
            System.out.println(accuracy);
            return accuracy;
        }
        System.out.println(winCount);
        return 0;
    }

    private static double[] optimize (double[] w) {
        KNN_combined knn = new KNN_combined(ProdSelection.class, 3);

        double[] wTemp  = new double[6];
        System.arraycopy(w, 0, wTemp, 0, 6);

        for (int i = 0; i < 6; i++) {
            WeightAndAccuracy optimal = null;
            double j = 0;
            ArrayList<Double> list = new ArrayList<>();
            while (!hasFinished(list)) {
                wTemp[i] = j;
                double accuracy = knn.crossValidation(wTemp);
                System.out.println(accuracy);
                System.out.println(Arrays.toString(wTemp));
                if (optimal == null) {
                    optimal = new WeightAndAccuracy(j, accuracy);
                } else {
                    if (accuracy > optimal.accuracy) {
                        optimal = new WeightAndAccuracy(j, accuracy);
                    }
                }

                list.add(accuracy);
                if (list.size() > DROP) {
                    list.remove(0);
                }
                j += 1;
            }
            wTemp[i] = optimal.weight;
            System.out.println(Arrays.toString(wTemp));
        }
        for (double d : wTemp) {
            System.out.print(d + ", ");
        }
        System.out.println("Accuracy = " + knn.crossValidation(wTemp));
        return wTemp;
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