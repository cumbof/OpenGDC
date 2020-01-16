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
    
    private static final String BASE_SEARCH_URL = "https://api.gdc.cancer.gov/files";
    private static final String USER_AGENT = "Mozilla/5.0";
    
    public static void main(String[] args) {
        try {
            String field1 = "cases.samples.portions.analytes.aliquots.aliquot_id";
            String value1 = "001201ec-e31a-4887-b4d7-9b4139b7cdf2";

            String field2 = "data_type";
            String value2 = "miRNA Expression Quantification";
            
            String postJsonData = "{" +
                        "\"filters\":{" +
                            "\"op\":\"and\"," +
                            "\"content\":[" +
                                "{" +
                                    "\"op\":\"in\"," +
                                    "\"content\":{" +
                                        "\"field\":\""+field1+"\"," +
                                        "\"value\":[" +
                                            "\""+value1+"\"" +
                                        "]" +
                                    "}" +
                                "}," +
                                "{" +
                                    "\"op\":\"in\"," +
                                    "\"content\":{" +
                                        "\"field\":\""+field2+"\"," +
                                        "\"value\":[" +
                                            "\""+value2+"\"" +
                                        "]" +
                                    "}" +
                                "}" +
                            "]" +
                        "}," +
                        "\"format\":\"json\"," +
                        "\"size\":\"10000\"," +
                        "\"pretty\":\"true\"," +
                        "\"fields\":\"cases.samples.portions.analytes.aliquots.aliquot_id,cases.samples.portions.analytes.aliquots.created_datetime,created_datetime\"" +
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
            //StringBuffer response = new StringBuffer();

            while ((output = in.readLine()) != null) {
                //response.append(output);
                System.out.println(output);
            }
            in.close();

            //printing result from response
            //System.out.println(response.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
