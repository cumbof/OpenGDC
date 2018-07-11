package opengdc.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 *
 * @author fabio
 */
public class GDCTestConnection {
    
    //private static final int SIZE_LIMIT = Integer.MAX_VALUE;
    private static final int SIZE_LIMIT = 5;
    private static final String PROJECT_ID = "TCGA-BRCA";
    private static final String DATA_TYPE = "Gene Expression Quantification";
    
    private static final String BASE_URL = "https://api.gdc.cancer.gov/";
    private static final String BASE_SEARCH_URL = BASE_URL+"files";
    private static final String USER_AGENT = "Mozilla/5.0";
    
    public static void main(String[] args) {
        try {
            String payload = "{" +
                                "\"filters\":{" +
                                    "\"op\":\"and\"," +
                                    "\"content\":[" +
                                        "{" +
                                            "\"op\":\"=\"," +
                                            "\"content\":{" +
                                                "\"field\":\"cases.project.project_id\"," +
                                                "\"value\":[" +
                                                    "\""+PROJECT_ID+"\"" +
                                                "]" + 
                                            "}" +
                                        "}," +
                                        "{" +
                                            "\"op\":\"=\"," +
                                            "\"content\":{" +
                                                "\"field\":\"files.data_type\"," +
                                                "\"value\":[" +
                                                    "\""+DATA_TYPE+"\"" +
                                                "]" + 
                                            "}" +
                                        "}," +
                                        "{" +
                                            "\"op\":\"=\"," +
                                            "\"content\":{" +
                                                "\"field\":\"access\"," +
                                                "\"value\":[" +
                                                    "\"open\"" +
                                                "]" + 
                                            "}" +
                                        "}" +
                                    "]" +
                                "}," +
                                "\"format\":\"json\"," +
                                "\"size\":\""+SIZE_LIMIT+"\"," +
                                "\"pretty\":\"true\"" +
                              "}";
            
            String url = BASE_SEARCH_URL;
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            // Setting basic post request
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type","application/json");

            // Send post request
            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(payload);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("Sending 'POST' request to URL : " + url);
            //System.out.println("Post Data : " + payload);
            System.out.println("Response Code : " + responseCode);
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            for (String line; (line = reader.readLine()) != null;)
                System.err.println(line);
            reader.close();
            
            con.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
