
/**
 * Created by liuyuan on 4/4/16.
 */
public class Test_2 {
    private static double[] w = {12.0, 11.0, 18.0, 64.0, 3.0, 1.0, 2.0, 28.0};
    //private static double[] w = {1,1,1,1,1,1,1,1};
    public static void main(String[] args) {
        KNN_combined knn = new KNN_combined(ProdIntro.class, 3);
        double accuracy = knn.crossValidation(w);
        //Result result= knn.predict();
        System.out.println(accuracy);
    }
}