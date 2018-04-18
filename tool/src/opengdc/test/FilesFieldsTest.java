/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.test;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 *
 * @author fabio
 */
public class FilesFieldsTest {
    
    // https://java2blog.com/how-to-send-http-request-getpost-in-java/
    
    private static final String BASE_SEARCH_URL = "https://gdc-api.nci.nih.gov/files";
    private static final String USER_AGENT = "Mozilla/5.0";
    
    public static void main(String[] args) {
        try {
            String value = "7abd0180-998c-4c7d-9e87-9684a356b7ed";
            String field = "cases.samples.portions.analytes.aliquots.aliquot_id";

            String postJsonData = "{" +
                        "\"filters\":{" +
                            "\"op\":\"and\"," +
                            "\"content\":[" +
                                "{" +
                                    "\"op\":\"in\"," +
                                    "\"content\":{" +
                                        "\"field\":\""+field+"\"," +
                                        "\"value\":[" +
                                            "\""+value+"\"" +
                                        "]" +
                                    "}" +
                                "}" +
                            "]" +
                        "}," +
                        "\"format\":\"json\"," +
                        "\"size\":\"10000\"" +
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
            wr.writeBytes(postJsonData);
            wr.flush();
            wr.close();

            int responseCode = con.getResponseCode();
            System.out.println("Sending 'POST' request to URL : " + url);
            System.out.println("Post Data : " + postJsonData);
            System.out.println("Response Code : " + responseCode);

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String output;
            StringBuffer response = new StringBuffer();

            while ((output = in.readLine()) != null) {
                response.append(output);
            }
            in.close();

            //printing result from response
            System.out.println(response.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
