/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gdc2bed.test;

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
    
    //private static final int size_limit = Integer.MAX_VALUE;
    private static final int size_limit = 5;
    private static final String project_id = "TCGA-BRCA";
    private static final String data_type = "Gene Expression Quantification";
    
    public static void main(String[] args) {
        try {
            String conn_str = "https://gdc-api.nci.nih.gov/files?from=1&size="+size_limit+"&pretty=true&filters=";
            String json_str = "{" +
                                  "\"op\":\"and\"," +
                                  "\"content\":[" +
                                      "{" +
                                          "\"op\":\"=\"," +
                                          "\"content\":{" +
                                              "\"field\":\"cases.project.project_id\"," +
                                              "\"value\":[" +
                                                  "\""+project_id+"\"" +
                                              "]" + 
                                          "}" +
                                      "}," +
                                      "{" +
                                          "\"op\":\"=\"," +
                                          "\"content\":{" +
                                              "\"field\":\"files.data_type\"," +
                                              "\"value\":[" +
                                                  "\""+data_type+"\"" +
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
