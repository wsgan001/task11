import java.security.KeyStore.PrivateKeyEntry;
import java.util.ArrayList;
import java.util.List;

import javax.print.attribute.standard.RequestingUserName;

import org.omg.PortableServer.ServantActivator;

/**
 * @author Yuheng Li
 * @version 1.0
 * @since Mar 28, 2016
 */
public class KNN {
    List<Element> trainData = new ArrayList<Element>();

    /**
     * constructor
     * Initialize a KNN instance
     * KNN k = new KNN();
     * set training set
     * k.setTrain("URL");
     * predict the test set
     * k.predict("URL");
     * return a predict result instance, with all predictions and a accuracy
     * 
     * 
     */
    public KNN() {
    }
    
    /**
     * train the KNN model using given training set
     * @param path training set file
     * @param isWeighted false: use even weight, otherwise, using 10 fold CV to calculate the best weight
     * @return return model
     */
    public Model train(String path, boolean isWeighted) {
        return new Model();
    }

    public Result predict(String path, Model model) {
        return new Result();
    }

    private void crossValidation(int fold) {

    }
    
    private double calculateSim(){
        return 2;
    }

    private List<Element> loadData(String path) {
        return trainData;
    }
}
