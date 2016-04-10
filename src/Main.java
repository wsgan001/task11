import java.text.DecimalFormat;


public class Main {
    public static void main(String[] args) {
        String train = "trainProdIntro.binary.arff";
        String test = "testProdIntro.binary.arff";
        String train2 = "trainProdSelection.arff";
        String test2 = "testProdSelection.arff";
        
        DecimalFormat df = new DecimalFormat("###.00");
        
        
        System.out.println("10 Fold Cross Validation result for train data ProdIntro is: ");
        System.out.println(df.format((KNN.crossvalidation(train, 10))*100) +"%");

        System.out.println("10 Fold Cross Validation result for train data ProdSelection is: ");
        System.out.println(df.format((KNN.crossvalidation(train2, 10))*100) +"%");
        double[] w = {10.0, 9.0, 0.0, 164.0, 1.0, 1.0, 23.0, 34.0};
        double[] w2 = {1.0, 0.0, 2.0, 39.0, 4.0, 27.0};
        System.out.println("The weight-optimized 10 Fold Cross Validation result for train data ProdIntro is: ");
        System.out.println(df.format(KNN.crossvalidation(train, 10, w) * 100) + "%");
        System.out.println("The weight-optimized 10 Fold Cross Validation result for train data ProdSelection is: ");
        System.out.println(df.format(KNN.crossvalidation(train2, 10, w2) * 100) + "%");
        
        System.out.println("The prediction for ProdSelection test data is ");
        System.out.println(df.format(KNN.validate(KNN.predict(train, test)) * 100) +"%");
        System.out.println("The prediction for ProdSelection test data is ");
        System.out.println(df.format(KNN.validate(KNN.predict(train2, test2)) * 100) +"%");
        
        
    }

}
