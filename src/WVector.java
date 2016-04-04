import java.util.*;

/**
 * Created by liuyuan on 4/3/16.
 */
public class WVector {
    private static double[] w = {0, 0, 0, 0, 0, 0};
    private static final int DROP = 200;

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
        for (int i = 0; i < 1000; i++) {
            KNN knn = new KNN();
            double scoreNew = knn.crossValidation(wNew);
            accuracy += scoreNew;
            // System.out.println("New score: " + scoreNew);
            double scoreOld = knn.crossValidation(wOld);
            // System.out.println("Old score: " + scoreOld);
            if (scoreNew > scoreOld) {
                winCount++;
            }
        }
        accuracy = accuracy / 1000;
        if (winCount >= 510) {
            System.out.println(winCount);
            System.out.println(accuracy);
            return accuracy;
        }
        System.out.println(winCount);
        return 0;
    }

    private static double[] optimize (double[] w) {
        KNN knn = new KNN();

        double[] wTemp  = new double[6];
        System.arraycopy(w, 0, wTemp, 0, 6);

        for (int i = 0; i < 6; i++) {
            WeightAndAccuracy optimal = null;
            double j = 0;
            ArrayList<Double> list = new ArrayList<>();
            while (!hasFinished(list)) {
//                for (double d : w) {
//                    System.out.print(d + "\t");
//                }
                wTemp[i] = j;
                double accuracy = knn.crossValidation(wTemp);
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
                // System.out.println("index= " + i + " weight = " + j + "\t accuracy = " + accuracy);
                j += 1;
//                for (Double d : list) {
//                    System.out.print(d + "\t");
//                }
            }
            wTemp[i] = optimal.weight;
        }
        for (double d : wTemp) {
            System.out.print(d + ", ");
        }
        System.out.println("Accuracy = " + knn.crossValidation(wTemp));
        return wTemp;
    }


    private static double[] optimizeReverse (double[] w) {
        KNN knn = new KNN();
        for (int i = 5; i > -1; i--) {
            WeightAndAccuracy optimal = null;
            double j = 0;
            ArrayList<Double> list = new ArrayList<>();
            while (!hasFinished(list)) {
                w[i] = j;
                double accuracy = knn.crossValidation(w);
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
                // System.out.println("index= " + i + " weight = " + j + "\t accuracy = " + accuracy);
                j += 1;
            }
            w[i] = optimal.weight;
        }
        for (double d : w) {
            System.out.print(d + ", ");
        }
        System.out.println(knn.crossValidation(w));
        return w;
    }

    static class WeightAndAccuracy {
        private double weight;
        private double accuracy;

        public WeightAndAccuracy (double weight, double accuracy) {
            this.weight = weight;
            this.accuracy = accuracy;
        }
    }

    static class WAndAccuracy {
        private double[] w;
        private double accuracy;

        public WAndAccuracy (double[] w, double accuracy) {
            this.w = w;
            this.accuracy = accuracy;
        }
    }
}
