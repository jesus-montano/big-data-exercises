package nearsoft.academy.bigdata.recommendation;


import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class MovieRecommender {
    public Map<String, Integer> mapProducts = new HashMap<String, Integer>();
    public Map<String, Integer> mapUsers = new HashMap<String, Integer>();
    private String path = "";
    private InputStream fileStream;
    private InputStream gzipStream;
    private Reader decoder;
    private BufferedReader buffered;
    String info = "";
    String inpArrReviews = "";
    int totalReviews = 0;
    private List<String> users = new ArrayList();
    private List<String> products = new ArrayList();
    private List<Double> score = new ArrayList();
    private List<String> usersR = new ArrayList();
    private List<String> productsR = new ArrayList();


    public MovieRecommender() {

        ///home/nsl-jmontano/Documents/java/big-data-exercises-master/src/test/java/nearsoft/academy/bigdata/recommendation/path/to/movies.txt.gz
    }

    public MovieRecommender(String path) {

        try {
            this.path = path;
            System.out.println("===========================" + this.path);
            fileStream = new FileInputStream(path);
            gzipStream = new GZIPInputStream(fileStream);
            decoder = new InputStreamReader(gzipStream);
            buffered = new BufferedReader(decoder);
            System.out.println(buffered);
            info = "";
            String[] array;
            while ((info = buffered.readLine()) != null) {
                if (info.startsWith("product/productId")) {
                    array = info.split(" ");
                    productsR.add(array[1]);
                    totalReviews++;
                    if (!products.contains(array[1])) {
                        products.add(array[1]);
                    }


                } else if (info.startsWith("review/userId")) {
                    array = info.split(" ");
                    usersR.add(array[1]);
                    if (!users.contains(array[1])) {
                        users.add(array[1]);
                    }


                } else if (info.startsWith("review/score:")) {
                    array = info.split(" ");

                    score.add(Double.parseDouble(array[1]));



                }


            }
            mapProducts = getMap(productsR);
            mapUsers = getMap(usersR);
            PrintWriter writer = new PrintWriter(new File("peliculas.csv"));
            for (int i = 0; i < productsR.size(); i++) {
                writer.write(mapUsers.get(usersR.get(i)) + "," + mapProducts.get(productsR.get(i)) + "," + score.get(i) + "\n");


            }
            writer.close();


        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    public int getTotalReviews() {


        return totalReviews;
    }

    public int getTotalProducts() {

        return products.size();
    }

    public int getTotalUsers() {

        return users.size();
    }

    public List<String> getRecommendationsForUser(String id) {

        List lstRecommender = new ArrayList<String>();


        try {
            DataModel model = new FileDataModel(new File("peliculas.csv"));
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);

            List<RecommendedItem> recommendations = recommender.recommend(mapUsers.get(id), 3);
            for (RecommendedItem recommendation : recommendations) {
                String sToAdd = getKeyFromProductMap((int) recommendation.getItemID());
                lstRecommender.add(sToAdd);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lstRecommender;
    }

    private Map<String, Integer> getMap(List<String> array) {


        Map<String, Integer> map = new HashMap<String, Integer>();
        int id = 1;
        for (String s : array) {
            if (!map.containsKey(s)) {
                map.put(s, id);
                id++;
            }
        }

        return map;
    }

    public String getKeyFromProductMap(int value) {

        for (String key : mapProducts.keySet()) {
            if (mapProducts.get(key) == value)
                return key;
        }
        return null;
    }


}
