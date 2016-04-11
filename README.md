# task11
Implementation of KNN in Java

HOW TO RUN:
1. Import the JAR file.
2. Run Main.java file. Can modify line 6-11 with the path of arff files. Line 6 and 7 should be the path of product Introduction binary labels files. Line 8 and 9 should be the path of production selection files. Line 10 and 11 should be the product introduction real number files. 

METHODS PROVIDED:
3. Can use crossvalidation(String file, int folder) or crossvalidation(String file, int folder, int[] w) to do crossvalidation for product intro train data or production selection train data. 
4. Can use predict(String trainFile, String testFile) to get predicted labels for product intro train data or production selection train data, and validate result using validate method(Result result).
5. Can use predict(String trainFile, String testFile) to get predicted revenue for test data.

