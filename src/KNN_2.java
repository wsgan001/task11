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
public class KNN_2 {
    private static final int LABLENUM = 2;
    private static final String TRAINPATH = "trainProdIntro.binary.arff";
    private static final String TESTPATH = "testProdIntro.binary.arff";
    private static int k = 3;
    private static final double[] DEFAULTWEIGHT = { 1, 1, 1, 1, 1, 1, 1, 1 };
    private List<ProdIntro> trainData = new ArrayList<ProdIntro>();
    private List<ProdIntro> testData = new ArrayList<ProdIntro>();
    private List<ProdIntro> shuffled = new ArrayList<ProdIntro>();

    ///////////////////////////////////////////////////////
    ////////////////// CONSTRUCTORS/////////////////////////
    ///////////////////////////////////////////////////////
    /**
     * Default Constructor, K = 3
     */
    public KNN_2() {
        this(3);
    }

    /**
     * Use customized K
     *
     * @param k
     */
    public KNN_2(int k) {
        this.k = k;
        try {
            loadData(TRAINPATH, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        shuffled = cloneList(trainData);
        Collections.shuffle(shuffled);
    }

    ///////////////////////////////////////////////////////
    ////////////////// Public Functions////////////////////
    ///////////////////////////////////////////////////////

    /**
     * Predict using default testing set path
     *
     * @return
     */
    public Result predict() {
        return predict(TRAINPATH);
    }

    /**
     * Use default weight and training set to predict the given test set
     *
     * @param path
     *            test set file path
     * @return
     */
    public Result predict(String path) {
        try {
            // load test data
            loadData(path, false);
            loadData(TRAINPATH, true);
            return predict(trainData, testData, DEFAULTWEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Predict the test result using given training set and weight
     *
     * @param train
     *            training set
     * @param test
     *            testing set
     * @param w
     *            weight
     * @return Result object
     */
    public Result predict(List<ProdIntro> train, List<ProdIntro> test,
            double[] w) {
        Result result = new Result();
        List<Integer> resultSet = new ArrayList<>();
        // for each element in test
        for (ProdIntro te : test) {
            // for each element in train
            PriorityQueue<Sim> queue = new PriorityQueue<>();
            for (ProdIntro tr : train) {
                // calculate the similarity of te and tr
                queue.offer(new Sim(calculateSim(te, tr, w), tr.getLabel()));
            }
            resultSet.add(findWinner(queue));
        }
        result.testSet = test;
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
        for (int i = 0; i < result.testSet.size(); i++) {
            if (result.testSet.get(i).getLabel() == result.resultSet.get(i)) {
                hit++;
            }
        }
        result.accuracy = hit / result.resultSet.size();
        return result.accuracy;
    }

    public double crossValidation(int fold) {
        return crossValidation(fold, DEFAULTWEIGHT);
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
        int size = trainData.size();
        if (fold > size)
            fold = size;
        List<ProdIntro> shuffled = cloneList(trainData);
        Collections.shuffle(shuffled);
        int testSize = size / fold;
        int mod = size % fold;
        int cnt = 0;
        int num = 1;
        int start = 0;
        double err = 0;
        while (start < size) {
            int end = start + testSize + (cnt++ < mod ? 1 : 0);
            List<ProdIntro> test = cloneList(shuffled.subList(start, Math.min(end, size)));
            List<ProdIntro> train = cloneList(shuffled.subList(0, start));
            train.addAll(cloneList(shuffled.subList(end, size)));
            double currErr = validate(predict(train, test, w));
//            System.out.println(
//            String.format("Round %d: %.2f%%", num++, currErr * 100));
            err += currErr;
            start = end;
        }
//        System.out.println(String.format("\nResult: %.2f%%", err/fold *
//        100));
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
    private List<ProdIntro> cloneList(List<ProdIntro> list) {
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
        List<ProdIntro> result = new ArrayList<ProdIntro>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("@") && !(line.length() == 0)) {
                result.add(new ProdIntro(line));
            }
        }
        if (isTrain) {
            trainData = result;
            ProdIntro.resetMinMax(trainData);
        } else {
            testData = result;
        }
        br.close();
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
    private double calculateSim(ProdIntro p1, ProdIntro p2, double[] w) {
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
//        KNN_2 test = new KNN_2();
//        // double[] weight = {0.002, 0.00, 0.006, 0.172, 0.013, 0.109};
//        double[] weight = { 1, 1, 1, 1, 1, 1, 1, 1 };
//        double result = test.crossValidation(weight);
//        // Result result = test.predict(TRAINPATH);
//        // test.validate(result);
//        // System.out.println(result.accuracy);
    }
}
