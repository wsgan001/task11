
/**
 * Created by liuyuan on 4/4/16.
 */
public class Test_2 {
    private static double[] w = {0.780, 8.297, 9.169, 8.102, 0.190, 0.054, 0.041, 5.795};
    //private static double[] w = {1,1,1,1,1,1,1,1};
    public static void main(String[] args) {
        KNN_2 knn = new KNN_2();
        double accuracy = knn.crossValidation(w);
        System.out.println(accuracy);
    }
}