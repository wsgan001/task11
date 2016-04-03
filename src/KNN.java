import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
    private List<ProdSelection> trainData;
    private double[] weights;
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
     * @throws IOException 
     */
    public void train(String path, double[] weights) throws IOException {
        loadTrainData(path);
    }
    public void train(double[] weights) throws IOException {
        train("trainProdSelection.arff", weights);
    }
    public void train() throws IOException {
        double[] defaultWeight = {1,1,1,1,1,1};
        train("trainProdSelection.arff", defaultWeight);
    }

    public Result predict(String path) {
        return new Result();
    }

    private void crossValidation(int fold) {

    }
    
    private double calculateSim(){
        return 2;
    }
    private void loadTrainData(String path) throws IOException {
        trainData = new ArrayList<ProdSelection>();
        BufferedReader br = new BufferedReader(new FileReader(path));
        String line;
        while((line = br.readLine()) != null) {
            trainData.add(new ProdSelection(line));
        }
        
    }
}
