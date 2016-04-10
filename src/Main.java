package knn;

import java.text.DecimalFormat;


public class Main {
    public static void main(String[] args) {
        String train = "/Users/linjiaruc/Documents/Java/BeginningJava/data mining/src/trainProdIntro.binary.arff";
        String test = "/Users/linjiaruc/Documents/Java/BeginningJava/data mining/src/testProdIntro.binary.arff";
        String train2 = "/Users/linjiaruc/Documents/Java/BeginningJava/data mining/src/trainProdSelection.arff";
        String test2 = "/Users/linjiaruc/Documents/Java/BeginningJava/data mining/src/testProdSelection.arff";
        
        DecimalFormat df = new DecimalFormat("###.00");
        
        
        System.out.println("10 Fold Cross Validation result for train data ProdIntro is: ");
        System.out.println(df.format((KNN.crossvalidation(train, 10))*100) +"%");

        System.out.println("10 Fold Cross Validation result for train data ProdSelection is: ");
        System.out.println(df.format((KNN.crossvalidation(train2, 10))*100) +"%");
        double[] w = {0.780, 8.297, 9.169, 8.102, 0.190, 0.054, 0.041, 5.795};
        double[] w2 = {0.002, 0.00, 0.006, 0.172, 0.013, 0.109};
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