import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * @author Yuheng Li
 * @version 1.0
 * @since Mar 28, 2016
 */
public class KNN {
    private static final int LABLENUM = 5;
    private static final String TRAINPATH = "trainProdSelection.arff";
    private static final String TESTPATH = "testProdSelection.arff";
    private static int k = 3;
    private static final double[] DEFAULTWEIGHT = { 1, 1, 1, 1, 1, 1 };
    private List<ProdSelection> trainData;
    private List<ProdSelection> testData;
    
    /**
     * Default Constructor, K = 3
     */
    public KNN() {
        this(3);
    }
    
    /**
     * Use customized K
     * @param k
     */
    public KNN(int k) {
        this.k = k;
    }
    
    /**
     * Use default weight and training set to predict the given test set
     * @param path test set file path
     * @return
     */
    public Result predict(String path) {
        try {
            // load test data
            loadData(path, false);
            loadData(TRAINPATH, true);
            return predict(trainData,testData,DEFAULTWEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Predict the test result using given training set and weight
     * @param train training set
     * @param test testing set
     * @param w weight
     * @return Result object
     */
    public Result predict(List<ProdSelection> train, List<ProdSelection> test,
            double[] w) {
        Result result = new Result();
        List<Integer> resultSet = new ArrayList<>();
        // for each element in test
        for (ProdSelection te : test) {
            // for each element in train
            PriorityQueue<Sim> queue = new PriorityQueue<>();
            for (ProdSelection tr : train) {
                // calculate the similarity of te and tr
                queue.offer(new Sim(calculateSim(te, tr, w), tr.getLabel()));
            }
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
            resultSet.add(winner);
        }
        result.testSet = test;
        result.resultSet = resultSet;
        return result;
    }
    
    /**
     * Predict using default testing set path
     * @return
     */
    public Result predict() {
        return predict(TESTPATH);
    }
    
    /**
     * Validate the predict accuracy using result object
     * @param result
     */
    public void validate(Result result) {
        double hit = 0;
        for(int i = 0; i < result.testSet.size(); i++) {
            if (result.testSet.get(i).getLabel() == result.resultSet.get(i)){
                hit++;
            }
        }
        result.accuracy = hit / result.resultSet.size();
    }

    private void crossValidation(int fold) {

    }
    
    /**
     * Load training set / testing set
     * @param path file path
     * @param isTrain training set: true, testing set: false
     * @throws IOException
     */
    private void loadData(String path, boolean isTrain) throws IOException {
        List<ProdSelection> result = new ArrayList<ProdSelection>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while ((line = br.readLine()) != null) {
            if (!line.startsWith("@") && !(line.length() == 0)) {
                result.add(new ProdSelection(line));
            }
        }
        if (isTrain) {
            trainData = result;
            ProdSelection.resetMinMax(trainData);
        } else {
            testData = result;
        }
    }

    /**
     * calculate the similarity of p1 and p2, regarding to weight vetor w
     * 
     * @param p1
     * @param p2
     * @return similarity between p1 and p2, from 0 to Double.MAX_VALUE;
     */
    private double calculateSim(ProdSelection p1, ProdSelection p2,
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

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
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
        KNN test = new KNN();
        Result result = test.predict();
        test.validate(result);
        System.out.println(result.accuracy);
    }
}
