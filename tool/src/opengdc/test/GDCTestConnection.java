package opengdc.test;

import java.io.BufferedReader;
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
    
    public static void main(String[] args) {
        try {
            String conn_str = "https://gdc-api.nci.nih.gov/files?from=1&size="+SIZE_LIMIT+"&pretty=true&filters=";
            String json_str = "{" +
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
                              "}";
            
            conn_str = conn_str + URLEncoder.encode(json_str, "UTF-8");
            System.err.println(conn_str);
            
            HttpURLConnection conn = (HttpURLConnection) (new URL(conn_str)).openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line; (line = reader.readLine()) != null;)
                    System.out.println(line);
            reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
