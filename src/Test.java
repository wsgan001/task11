/**
 * Created by liuyuan on 4/4/16.
 */
public class Test {
    private static double[] w = {3.0, 0.0, 9.0, 210.0, 23.0, 125.0};
    public static void main(String[] args) {
        KNN knn = new KNN();
        double accuracy = knn.crossValidation(w);
        System.out.println(accuracy);
    }
}
