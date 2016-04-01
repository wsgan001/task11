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
     * 
     */
    public KNN() {
    }

    public void train(String path) {
        loadData(path);

    }

    public void predict(String path) {

    }

    public void crossValidation(int fold) {

    }
    
    public double calculateSim(){
        return 2;
    }

    private List<Element> loadData(String path) {
        return trainData;
    }
}
