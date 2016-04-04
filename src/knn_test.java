
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

public class knn_test {
    public static void main(String[] args) throws Exception {
        
        FileReader in_train = new FileReader("trainProdSelection.arff");        
        FileReader in_test = new FileReader("testProdSelection.arff");        
        
        BufferedReader br_train = new BufferedReader(in_train);
        BufferedReader br_test = new BufferedReader(in_test);
        /*
         * load train data
         */
        String line;

        ArrayList<String> newList = new ArrayList<String>();
        while ((line = br_train.readLine()) != null) {
            if  (!line.startsWith("@") && !(line.length()==0)) {
               //   System.out.println(line);
                newList.add(line);
            }
        }
        br_train.close();
        /*
        * load test data
        */
        String line2;
        ArrayList<String> newList2 = new ArrayList<String>();
        while ((line2 = br_test.readLine()) != null) {
            if (!line2.startsWith("@") && !(line2.length()==0)) {
                //   System.out.println(line);
                newList2.add(line2);
               // System.out.println(Arrays.toString(tokens));
            }
        }
        br_test.close();
        
        int K = 3;
        int nAttrib = 6;  
        int TrainSize = newList.size();    // Number of Train Data

        int ntestData = newList2.size();     // Number of Test Data
        System.out.println(ntestData);
        // Read Train Data with nAttribe Features
        Collections.shuffle(newList);
        
        int nTrainData = (int) (TrainSize*0.9);// 90% train
        ntestData = TrainSize - nTrainData; //10% test
        ArrayList<DataObject> data = new ArrayList<DataObject>();
        
        for(int i = 0; i < nTrainData; i++) {
            String temp = newList.get(i).toString();
            Element element = new Element(temp);
            DataObject ob_train = new DataObject(element);
            data.add(ob_train);
        }
        System.out.println(data.size());
        // Read Test Data 
        ArrayList<DataObject> testData = new ArrayList<DataObject>();
        //   ntestData = 1;
        for(int i = nTrainData; i < TrainSize; i++) {
            String temp = newList.get(i).toString();
            Element element = new Element(temp);
            DataObject ob_test = new DataObject(element);
            ob_test.trueLabel = element.getLabel();
            testData.add(ob_test);
        }
        System.out.println(testData);
        int count = 0;
        // Compute for the Distance of all the Test Data
        for(int z = 0; z < ntestData; z++) {
            for(int i = 0; i < nTrainData; i++) {
                Element train = data.get(i).getElement();
                Element test = testData.get(z).getElement();
                double dist =  0;
                if (train.getType() != test.getType()) {
                    dist += 1;
                    
                }
                if (train.getStyle() != test.getStyle()) {
                    dist += 1;
                }
                dist += Math.pow(train.getVacation() - test.getVacation(),2);
                dist += Math.pow(train.getCredit() - test.getCredit(),2);
                dist += Math.pow(train.getSalary() - test.getSalary(),2);
                dist += Math.pow(train.getProperty() - test.getProperty(),2);
                if (dist == 0) {
                    dist = 1000000;
                    
                } else {
                    dist = 1/Math.sqrt(dist);
                }
                data.get(i).dist = dist;
            }
            Collections.sort(data);
            //  System.out.println(data);
            
            //  Rank all the K neighbors
            HashMap<String, Double> gMode = new HashMap<String, Double>();
            double val = data.get(0).dist;
            gMode.put(data.get(0).getElement().getLabel(), data.get(0).dist);
            for(int i = 1, rank = 1; i < nTrainData && rank < K; i++) {
                if (val >= data.get(i).dist) {
                    rank++;
                    String temp_label = data.get(i).getElement().getLabel();
                 //   System.out.println(gMode.get(data.get(i).getElement().getLabel()));
                    if (gMode.containsKey(temp_label)) {
                        double temp_dist = gMode.get(data.get(i).getElement().getLabel());
                        gMode.put(temp_label, temp_dist + data.get(i).dist);
                    } else{
                        gMode.put(temp_label, data.get(i).dist);
                    }
                }
            }
            // Get K for test data
            testData.get(z).getElement().setLabel(getMode(gMode));        // Find the mode of the neighbors
            System.out.println("The test data" + z +" is classified as: "+testData.get(z).getElement().getLabel());
            System.out.println("The true label of test data" + z +" is " + testData.get(z).trueLabel());
           
            if (testData.get(z).trueLabel().equals(testData.get(z).getElement().getLabel())){
                count++;
            }
        }
        System.out.println("The accurency: "+ (double)count/ntestData);
    }
    public static String getMode(HashMap<String, Double> gMode) {
        String maxMode = null;
        double maxCount = 0;
        for(String d : gMode.keySet()) {
            double tCount = gMode.get(d);
            if (tCount > maxCount) {
                maxCount = tCount;
                maxMode = d;
            }
        }
        return maxMode;
    }
}
