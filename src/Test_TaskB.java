
/**
 * Created by liuyuan on 4/4/16.
 */
public class Test_TaskB {
    // 10.0, 9.0, 0.0, 164.0, 1.0, 1.0, 23.0, 34.0
    private static double[] w = {2.0, 4.0, 3.0, 43.0, 1.0, 1.0, 2.0, 32.0};
    // private static double[] w = {1,1,1,1,1,1,1,1};
    public static void main(String[] args) {
        KNN knn = new KNN(ProdIntro.class, 3);
        double accuracy = knn.crossValidation(w);
        //Result result= knn.predict();
        System.out.println(accuracy);
    }
}