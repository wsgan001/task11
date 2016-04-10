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
public class KNN_combined {
    private static final int LABLENUM = 5;
    private static String TRAINPATH;
    private static String TESTPATH;
    private static List<ProdSelection> selectionTrain;
    private static List<ProdIntro> introTrain;
    private static List<ProdSelection> selectionTest;
    private static List<ProdIntro> introTest;
    private static List<ProdSelection> selectionShuffled;
    private static List<ProdIntro> introShuffled;
    private static int k = 3;
   
    ///////////////////////////////////////////////////////
    ////////////////// CONSTRUCTORS/////////////////////////
    ///////////////////////////////////////////////////////
    /**
     * Default Constructor, K = 3
     */
    public KNN_combined() {
    }

    /**
     * Use customized K
     *
     * @param k
     */
    public KNN_combined(Object obj, int k) {
        if (obj == ProdIntro.class) {
            TRAINPATH = "trainProdIntro.binary.arff";
            TESTPATH = "testProdIntro.binary.arff";
            introTrain = new ArrayList<ProdIntro>();
            introTest = new ArrayList<ProdIntro>();
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
            try {
                loadData(TRAINPATH, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            selectionShuffled = cloneSelectionList(selectionTrain);
            Collections.shuffle(selectionShuffled);
        }
        this.k = k;
    }

    ///////////////////////////////////////////////////////
    ////////////////// Public Functions////////////////////
    ///////////////////////////////////////////////////////

    /**
     * Use default weight and training set to predict the given test set
     *
     * @param path
     *            test set file path
     * @return
     */
    public Result predict(String path, double[] weight) {
        try {
            // load test data
            loadData(path, false);
            loadData(TRAINPATH, true);
            if (TRAINPATH == "trainProdIntro.binary.arff") {
                return introPredict(introTrain, introTest, weight);
            }
            if (TRAINPATH == "trainProdSelection.arff") {
                return selectionPredict(selectionTrain, selectionTest, weight);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
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
    public Result introPredict(List<ProdIntro> train, List<ProdIntro> test,
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
    public Result selectionPredict(List<ProdSelection> train, List<ProdSelection> test,
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
    public double validate(Result result) {
        double hit = 0;
        if (TRAINPATH == "trainProdIntro.binary.arff") {
            for (int i = 0; i < result.introTestSet.size(); i++) {
                if (result.introTestSet.get(i).getLabel() == result.resultSet.get(i)) {
                    hit++;
                }
            }
        }
        if (TRAINPATH == "trainProdSelection.arff") {
            for (int i = 0; i < result.selectionTestSet.size(); i++) {
                if (result.selectionTestSet.get(i).getLabel() == result.resultSet.get(i)) {
                    hit++;
                }
            }
        }
        result.accuracy = hit / result.resultSet.size();
        return result.accuracy;
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

    /**
     * Conduct x fold cross validation with weight w
     * 
     * @param fold
     * @param w
     * @return
     */
    public double crossValidation(int fold, double[] w) {
        // try {
        // loadData(TRAINPATH, true);
        // } catch (IOException e) {
        // e.printStackTrace();
        // }
        double err = 0;
        if (TRAINPATH == "trainProdIntro.binary.arff") {
            int size = introTrain.size();
            if (fold > size)
                fold = size;
            List<ProdIntro> shuffled = introShuffled;
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
        if (TRAINPATH == "trainProdSelection.arff") {
            int size = selectionTrain.size();
            if (fold > size)
                fold = size;
            List<ProdSelection> shuffled = selectionShuffled;
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

    ///////////////////////////////////////////////////////
    ////////////////// PRIVATE FUNCTIONS///////////////////
    ///////////////////////////////////////////////////////
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
     * Load training set / testing set
     *
     * @param path
     *            file path
     * @param isTrain
     *            training set: true, testing set: false
     * @throws IOException
     */
    private void loadData(String path, boolean isTrain) throws IOException {
        if (TRAINPATH == "trainProdIntro.binary.arff") {
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
        if (TRAINPATH == "trainProdSelection.arff") {
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

    // testing method, using all default settings.
    public static void main(String[] args) {
//        // default
        KNN_combined knn = new KNN_combined(ProdIntro.class, 3);
        double[] weight = {2.0, 4.0, 3.0, 43.0, 1.0, 1.0, 2.0, 32.0};
        Result result = knn.predict(TESTPATH, weight);
//        // test.validate(result);
        for(Integer i : result.resultSet) {
            System.out.println(i);
        }
        System.out.println("-----------------------------------------");
        KNN_combined knn_2 = new KNN_combined(ProdSelection.class, 3);
        double[] weight_2 = {2.0, 0.0, 6.0, 172.0, 13.0, 109.0};
        result = knn_2.predict(TESTPATH, weight_2);
//        // test.validate(result);
        System.out.println(result.accuracy);
        for(Integer i : result.resultSet) {
            System.out.println(i);
        }
    }
}
