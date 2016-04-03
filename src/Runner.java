
/**
 * @author Yuheng Li
 * @version 1.0
 * @since Mar 28, 2016
 */
public class Runner {

    /**
     * @param args
     */
    public static void main(String[] args) {
        KNN model = new KNN();
        model.train(path, isWeighted);
    }

}
