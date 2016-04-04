
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
        KNN test = new KNN();
        Result result = test.predict();
        test.validate(result);
        System.out.println(result.accuracy);
    }

}
