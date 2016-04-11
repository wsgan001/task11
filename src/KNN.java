import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author Yuheng Li
 * @version 1.0
 * @since Mar 28, 2016
 */
public class KNN {
    private static int LABLENUM;
    private static String TRAINPATH;
    private static String TESTPATH;
    private static List<ProdSelection> selectionTrain;
    private static List<ProdIntro> introTrain;
    private static List<ProdIntroReal> realTrain;
    private static List<ProdSelection> selectionTest;
    private static List<ProdIntro> introTest;
    private static List<ProdIntroReal> realTest;
    private static List<ProdSelection> selectionShuffled;
    private static List<ProdIntro> introShuffled;
    private static List<ProdIntroReal> realShuffled;
    
    private static int k = 3;
    private static final double[] SelectionDEFAULTWEIGHT = { 1, 1, 1, 1, 1, 1};
    private static final double[] IntroDEFAULTWEIGHT = { 1, 1, 1, 1, 1, 1, 1, 1 };
    private static final double[] realDEFAULTWEIGHT = {10.0, 9.0, 0.0, 164.0, 1.0, 1.0, 23.0, 34.0};;
    
    ///////////////////////////////////////////////////////
    ////////////////// CONSTRUCTORS/////////////////////////
    ///////////////////////////////////////////////////////
    /**
     * Default Constructor, K = 3
     */
    public KNN(Class obj) {
        this(obj, 3);
    }
    /**
     * Use customized K
     *
     * @param k
     */
    public KNN(Class obj, int k) {
        if (obj == ProdIntro.class) {
            TRAINPATH = "trainProdIntro.binary.arff";
            TESTPATH = "testProdIntro.binary.arff";
            introTrain = new ArrayList<ProdIntro>();
            introTest = new ArrayList<ProdIntro>();
            LABLENUM = 2;
            k=5;
            introShuffled = new ArrayList<ProdIntro>();
            try {
                loadData(TRAINPATH, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            introShuffled = cloneIntroList(introTrain);
            Collections.shuffle(introShuffled);
        }
        if (obj == ProdSelection.class) {
            TRAINPATH = "trainProdSelection.arff";
            TESTPATH = "testProdSelection.arff";
            selectionTrain = new ArrayList<ProdSelection>();
            selectionTest = new ArrayList<ProdSelection>();
            LABLENUM = 5;
            selectionShuffled = new ArrayList<ProdSelection>();
            try {
                loadData(TRAINPATH, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            selectionShuffled = cloneSelectionList(selectionTrain);
            Collections.shuffle(selectionShuffled);
        } if (obj == ProdIntroReal.class) {
            TRAINPATH = "trainProdIntro.real.arff";
            TESTPATH = "testProdIntro.real.arff";
            realTrain = new ArrayList<ProdIntroReal>();
            realTest = new ArrayList<ProdIntroReal>();
            LABLENUM = 1;
            k=5;
            realShuffled = new ArrayList<ProdIntroReal>();
            try {
                loadData(TRAINPATH, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            realShuffled = cloneRealList(realTrain);
            Collections.shuffle(realShuffled);
        }
        KNN.k = k;
    }
    ///////////////////////////////////////////////////////
    ////////////////// Public Functions////////////////////
    ///////////////////////////////////////////////////////
    public static Result predict(String trainPath, String testPath) {
        if (trainPath.contains("binary")) {
            KNN knn_ProdIntro = new KNN(ProdIntro.class);
            return knn_ProdIntro.predict_path(trainPath, testPath);
        } if (trainPath.contains("real")) {
            KNN knn_ProdIntroreal = new KNN(ProdIntroReal.class);
            return knn_ProdIntroreal.predict_path(trainPath, testPath);
        } else {
            KNN knn_Selection = new KNN(ProdSelection.class);
            return knn_Selection.predict_path(trainPath, testPath);
        }
    }
    public static double validate(Result result) {
        double hit = 0;
        if (LABLENUM == 2) {
            for (int i = 0; i < result.introTestSet.size(); i++) {
                if (result.introTestSet.get(i).getLabel() == result.resultSet.get(i)) {
                    hit++;
                }
            }
          //  result.accuracy = hit / result.resultSet.size();
        } else if (LABLENUM == 5) {
            for (int i = 0; i < result.selectionTestSet.size(); i++) {
                
                if (result.selectionTestSet.get(i).getLabel() == result.resultSet.get(i)) {
                    hit++;
                }
            }
         //  result.accuracy = hit / result.resultSet.size();
        }
        result.accuracy = hit / result.resultSet.size();
        return result.accuracy;
    }
    
    

    public static double crossvalidation(String TrainPath, int fold) {
        if (TrainPath.contains("binary")) {
            KNN knn_ProdIntro = new KNN(ProdIntro.class);
            return knn_ProdIntro.crossValidation(fold);
        } else {
            KNN knn_ProdSelection = new KNN(ProdSelection.class);
            return knn_ProdSelection.crossValidation(fold);
        }
    }
    public static double crossvalidation(String TrainPath, int fold, double[] w) {
        if (TrainPath.contains("binary")) {
            KNN knn_ProdIntro = new KNN(ProdIntro.class);
            return knn_ProdIntro.crossValidation(fold, w);
        } else {
            KNN knn_ProdSelection = new KNN(ProdSelection.class);
            return knn_ProdSelection.crossValidation(fold, w);
        }
    }
    /**
     * Take a weight vecotr and conduct 10 fold validation, return an average
     * accuracy.
     *
     * @param w
     *            weight vector, size = 6
     * @return average accuracy
     */
    public double crossValidation(double[] w) {
        return crossValidation(10, w);
    }
    ///////////////////////////////////////////////////////
    ////////////////// PRIVATE FUNCTIONS///////////////////
    ///////////////////////////////////////////////////////
    /**
     * Predict using default testing set path
     *
     * @return
     */
    private Result predict() {
        return predict(TRAINPATH);
    }
    /**
     * Use default weight and training set to predict the given test set
     *
     * @param path
     *            test set file path
     * @return
     */
    private Result predict(String path) {
        try {
            // load test data
            loadData(path, false);
            loadData(TRAINPATH, true);
            if (LABLENUM == 2) {
                return introPredict(introTrain, introTest, IntroDEFAULTWEIGHT);
            }
            if (LABLENUM == 5) {
                return selectionPredict(selectionTrain, selectionTest, SelectionDEFAULTWEIGHT);
            } else {
                return realPredict(realTrain, realTest, IntroDEFAULTWEIGHT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     *
     * @param trainData trainData path.
     * @param testData testData path.
     * 
     *            
     * @return Result
     */
    private Result predict_path(String trainPath, String testPath) {
        try {
            // load test data
            loadData(testPath, false);
            loadData(trainPath, true);
            if (LABLENUM == 2) {
                return introPredict(introTrain, introTest, IntroDEFAULTWEIGHT);
            }
            if (LABLENUM == 5) {
                return selectionPredict(selectionTrain, selectionTest, SelectionDEFAULTWEIGHT);
            } else {
                return realPredict(realTrain, realTest, realDEFAULTWEIGHT);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    /**
     * Predict the test result using ProdIntro training set and weight
     *
     * @param train
     *            training set
     * @param test
     *            testing set
     * @param w
     *            weight
     * @return Result object
     */
    private Result introPredict(List<ProdIntro> train, List<ProdIntro> test,
            double[] w) {
        Result result = new Result();
        List<Integer> resultSet = new ArrayList<>();
        // for each element in test
        for (ProdIntro te : test) {
            // for each element in train
            PriorityQueue<Sim> queue = new PriorityQueue<>();
            for (ProdIntro tr : train) {
                // calculate the similarity of te and tr
                queue.offer(new Sim(calculateIntroSim(te, tr, w), tr.getLabel()));
            }
            resultSet.add(findWinner(queue));
        }
        result.introTestSet = test;
        result.resultSet = resultSet;
        return result;
    }
    private Result realPredict(List<ProdIntroReal> train, List<ProdIntroReal> test,
            double[] w) {
        Result result = new Result();
        List<Double> resultSet = new ArrayList<>();
        // for each element in test
        for (ProdIntroReal te : test) {
            // for each element in train
            PriorityQueue<SimforReal> queue = new PriorityQueue<>();
            for (ProdIntroReal tr : train) {
                // calculate the similarity of te and tr
                queue.offer(new SimforReal(calculateIntroSim(te, tr, w), tr.getRevenue()));
            }
            resultSet.add(findWinnerReal(queue));
            
        }
        for (int i = 0; i< resultSet.size(); i++) {
            System.out.printf("The " + (i+1) + "th test data's revenue is: ");
            System.out.printf("%.2f",resultSet.get(i));
            System.out.println();
        }
        result.introRealTestSet = test;
        result.resultRealSet = resultSet;
        return result;
    }
    /**
     * Predict the test result using ProdSelection training set and weight
     *
     * @param train
     *            training set
     * @param test
     *            testing set
     * @param w
     *            weight
     * @return Result object
     */
    private Result selectionPredict(List<ProdSelection> train, List<ProdSelection> test,
            double[] w) {
        Result result = new Result();
        List<Integer> resultSet = new ArrayList<>();
        // for each element in test
        for (ProdSelection te : test) {
            // for each element in train
            PriorityQueue<Sim> queue = new PriorityQueue<>();
            for (ProdSelection tr : train) {
                // calculate the similarity of te and tr
                queue.offer(new Sim(calculateSelectionSim(te, tr, w), tr.getLabel()));
            }
            resultSet.add(findWinner(queue));
        }
        result.selectionTestSet = test;
        result.resultSet = resultSet;
        return result;
    }
    /**
     * Validate the predict accuracy using result object
     *
     * @param result
     */
 
    private double crossValidation(int fold) {
        if (LABLENUM == 2) {
            return crossValidation(fold, IntroDEFAULTWEIGHT);
        } else {
            return crossValidation(fold, SelectionDEFAULTWEIGHT);
        }
    }

    /**
     * Conduct x fold cross validation with weight w
     * 
     * @param fold
     * @param w
     * @return
     */
    private double crossValidation(int fold, double[] w) {
        // try {
        // loadData(TRAINPATH, true);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        double err = 0;
        if (LABLENUM == 2) {
            int size = introTrain.size();
            if (fold > size)
                fold = size;
            List<ProdIntro> shuffled = cloneIntroList(introTrain);
            Collections.shuffle(shuffled);
            int testSize = size / fold;
            int mod = size % fold;
            int cnt = 0;
            int num = 1;
            int start = 0;
            
            while (start < size) {
                int end = start + testSize + (cnt++ < mod ? 1 : 0);
                List<ProdIntro> test = cloneIntroList(shuffled.subList(start, Math.min(end, size)));
                List<ProdIntro> train = cloneIntroList(shuffled.subList(0, start));
                train.addAll(cloneIntroList(shuffled.subList(end, size)));
                double currErr = validate(introPredict(train, test, w));
//                System.out.println(
//                String.format("Round %d: %.2f%%", num++, currErr * 100));
                err += currErr;
                start = end;
            }
//            System.out.println(String.format("\nResult: %.2f%%", err/fold *
//            100));
        }
        if (LABLENUM == 5) {
            int size = selectionTrain.size();
            if (fold > size)
                fold = size;
            List<ProdSelection> shuffled = cloneSelectionList(selectionTrain);
            Collections.shuffle(shuffled);
            int testSize = size / fold;
            int mod = size % fold;
            int cnt = 0;
            int num = 1;
            int start = 0;
            while (start < size) {
                int end = start + testSize + (cnt++ < mod ? 1 : 0);
                List<ProdSelection> test = cloneSelectionList(shuffled.subList(start, Math.min(end, size)));
                List<ProdSelection> train = cloneSelectionList(shuffled.subList(0, start));
                train.addAll(cloneSelectionList(shuffled.subList(end, size)));
                double currErr = validate(selectionPredict(train, test, w));
//                System.out.println(
//                String.format("Round %d: %.2f%%", num++, currErr * 100));
                err += currErr;
                start = end;
            }
//            System.out.println(String.format("\nResult: %.2f%%", err/fold *
//            100));
        }
        return err / fold;
    }

    /**
     * assign a label for given candidate queue.
     *
     * @param queue
     * @return
     */
    private int findWinner(PriorityQueue<Sim> queue) {
        // find the winner
        int winner = 0;
        int[] counter = new int[LABLENUM + 1];
        int i = 0;
        while (i++ < k) {
            int curr = queue.poll().label;
            counter[curr]++;
            if (counter[curr] > counter[winner]) {
                winner = curr;
            }
            if (counter[curr] > (k / 2))
                break;
        }
        return winner;
    }
    private double findWinnerReal(PriorityQueue<SimforReal> queue) {
        // find the winner
        double winner = 0;
        int j = 0;
        while (j < k) {
            double curr = queue.poll().revenue;
            winner += curr;
            j++;
        }
        return winner/j;
    }
    private List<ProdIntroReal> cloneRealList(List<ProdIntroReal> list) {
        List<ProdIntroReal> newList = new ArrayList<>();
        for (ProdIntroReal p : list) {
            newList.add(new ProdIntroReal(p));
        }
        return newList;
    }
    /**
     * deep copy a List of ProdSelection.
     * 
     * @param list
     * @return
     */
    private List<ProdSelection> cloneSelectionList(List<ProdSelection> list) {
        List<ProdSelection> newList = new ArrayList<>();
        for (ProdSelection p : list) {
            newList.add(new ProdSelection(p));
        }
        return newList;
    }
    /**
     * deep copy a List of ProdIntro.
     * 
     * @param list
     * @return
     */
    private List<ProdIntro> cloneIntroList(List<ProdIntro> list) {
        List<ProdIntro> newList = new ArrayList<>();
        for (ProdIntro p : list) {
            newList.add(new ProdIntro(p));
        }
        return newList;
    }
    /**
     * deep copy a List of ProdIntroReal.
     * 
     * @param list
     * @return
     */
    /**
     * Load training set / testing set
     *
     * @param path
     *            file path
     * @param isTrain
     *            training set: true, testing set: false
     * @throws IOException
     */
    private void loadData(String path, boolean isTrain) throws IOException {
        if (LABLENUM == 2) {
            List<ProdIntro> result = new ArrayList<ProdIntro>();
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("@") && !(line.length() == 0)) {
                    result.add(new ProdIntro(line));
                }
            }
            if (isTrain) {
                introTrain = result;
                ProdIntro.resetMinMax(introTrain);
            } else {
                introTest = result;
            }
            br.close();
        }
        if (LABLENUM == 5) {
            List<ProdSelection> result = new ArrayList<ProdSelection>();
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("@") && !(line.length() == 0)) {
                    result.add(new ProdSelection(line));
                }
            }
            if (isTrain) {
                selectionTrain = result;
                ProdSelection.resetMinMax(selectionTrain);
            } else {
                selectionTest = result;
            }
            br.close();
        } else {
            List<ProdIntroReal> result = new ArrayList<ProdIntroReal>();
            BufferedReader br = new BufferedReader(new FileReader(path));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.startsWith("@") && !(line.length() == 0)) {
                    result.add(new ProdIntroReal(line));
                }
            }
            if (isTrain) {
                realTrain = result;
                ProdIntroReal.resetMinMax(realTrain);
            } else {
                realTest = result;
            }
            br.close();
        }
    }

    /**
     * calculate the similarity of p1 and p2 of ProdIntro class given simMatrix
     * and weight w
     *
     * @param p1
     *            ProdIntro
     * @param p2
     *            ProdIntro
     * @return similarity between p1 and p2, from 0 to Double.MAX_VALUE;
     */
    private double calculateIntroSim(ProdIntro p1, ProdIntro p2, double[] w) {
        double dist = 0;
        dist += p1.getType().compartTo(p2.getType()) * w[0];
        dist += p1.getCustomer().compartTo(p2.getCustomer()) * w[1];
        dist += Math.pow(p1.getNFee() - p2.getNFee(), 2) * w[2];
        dist += Math.pow(p1.getNBudget() - p2.getNBudget(), 2) * w[3];
        dist += p1.getSize().compartTo(p2.getSize()) * w[4];
        dist += p1.getPromotion().compartTo(p2.getPromotion()) * w[5];
        dist += Math.pow(p1.getNRate() - p2.getNRate(), 2) * w[6];
        dist += Math.pow(p1.getNPeriod() - p2.getNPeriod(), 2) * w[7];
        if (dist == 0) {
            return Double.MAX_VALUE;
        } else {
            return 1 / Math.sqrt(dist);
        }
    }
    private double calculateIntroSim(ProdIntroReal p1, ProdIntroReal p2, double[] w) {
        double dist = 0;
        dist += p1.getType().compartTo(p2.getType()) * w[0];
        dist += p1.getCustomer().compartTo(p2.getCustomer()) * w[1];
        dist += Math.pow(p1.getNFee() - p2.getNFee(), 2) * w[2];
        dist += Math.pow(p1.getNBudget() - p2.getNBudget(), 2) * w[3];
        dist += p1.getSize().compartTo(p2.getSize()) * w[4];
        dist += p1.getPromotion().compartTo(p2.getPromotion()) * w[5];
        dist += Math.pow(p1.getNRate() - p2.getNRate(), 2) * w[6];
        dist += Math.pow(p1.getNPeriod() - p2.getNPeriod(), 2) * w[7];
        if (dist == 0) {
            return Double.MAX_VALUE;
        } else {
            return 1 / Math.sqrt(dist);
        }
    }
    /**
     * calculate the similarity of ProdSelection p1 and p2, regarding to weight vetor w
     * 
     * @param p1
     * @param p2
     * @return similarity between p1 and p2, from 0 to Double.MAX_VALUE;
     */
    private double calculateSelectionSim(ProdSelection p1, ProdSelection p2,
            double[] w) {
        double dist = 0;
        if (p1.getType() != p2.getType()) {
            dist += 1 * w[0];
        }
        if (p1.getStyle() != p2.getStyle()) {
            dist += 1 * w[1];
        }
        dist += Math.pow(p1.getNVacation() - p2.getNVacation(), 2) * w[2];
        dist += Math.pow(p1.getNCredit() - p2.getNCredit(), 2) * w[3];
        dist += Math.pow(p1.getNSalary() - p2.getNSalary(), 2) * w[4];
        dist += Math.pow(p1.getNProperty() - p2.getNProperty(), 2) * w[5];
        if (dist == 0) {
            return Double.MAX_VALUE;
        } else {
            return 1 / Math.sqrt(dist);
        }
    }

    /**
     * similarity object, stores current predicted label and score
     *
     * @author Yuheng Li
     * @version 1.0
     * @since Apr 3, 2016
     */
    private class Sim implements Comparable<Sim> {
        double score;
        int label;

        public Sim(double s, int l) {
            this.score = s;
            this.label = l;
        }

        @Override
        public int compareTo(Sim o) {
            if (this.score == o.score) {
                return Integer.compare(this.label, o.label);
            } else {
                return -Double.compare(this.score, o.score);
            }
        }
    }
    private class SimforReal implements Comparable<SimforReal> {
        double score;
        double revenue;

        public SimforReal(double s, double l) {
            this.score = s;
            this.revenue = l;
        }

        @Override
        public int compareTo(SimforReal o) {
            if (this.score == o.score) {
                return Double.compare(this.revenue, o.revenue);
            } else {
                return -Double.compare(this.score, o.score);
            }
        }
    }
    // testing method, using all default settings.
    public static void main(String[] args) {
//        // default
//        KNN_2 test = new KNN_2();
//        // double[] weight = {0.002, 0.00, 0.006, 0.172, 0.013, 0.109};
//        double[] weight = { 1, 1, 1, 1, 1, 1, 1, 1 };
//        double result = test.crossValidation(weight);
//        // Result result = test.predict(TRAINPATH);
//        // test.validate(result);
//        // System.out.println(result.accuracy);
    }
}
