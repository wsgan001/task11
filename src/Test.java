/**
 * Created by liuyuan on 4/10/16.
 */
public class Test {
    private static double[] w = {2.0, 0.0, 6.0, 172.0, 13.0, 109.0};
    //private static double[] w = {1,1,1,1,1,1,1,1};
    public static void main(String[] args) {
        KNN knn = new KNN();
        double accuracy = knn.crossValidation(w);
        //Result result= knn.predict();
        System.out.println(accuracy);

        KNN_combined knn_combined = new KNN_combined(ProdSelection.class, 3);
        accuracy = knn_combined.crossValidation(w);
        System.out.println(accuracy);
    }
}
