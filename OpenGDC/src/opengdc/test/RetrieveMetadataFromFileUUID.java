/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
public class RetrieveMetadataFromFileUUID {
    
    /*
    * https://docs.gdc.cancer.gov/API/Users_Guide/Search_and_Retrieval/#example-http-post-request
    */
    
    private static final int SIZE_LIMIT = Integer.MAX_VALUE;
    private static final String FILES_UUID = "\"0c7752f2-252b-45a6-a088-70b1baaeda78\", \"0b50ee5b-3130-4401-84b1-630596632a12\"";
    
    public static void main(String[] args) {
        try {
            String conn_str = "https://gdc-api.nci.nih.gov/files?from=1&pretty=true&";
            String json_str = "{" +
                                "\"filters\":{" +
                                    "\"op\":\"in\"," +
                                    "\"content\":{" +
                                        "\"field\":\"files.file_id\"," +
                                        "\"value\":[" +
                                            FILES_UUID +
                                        "]" +
                                    "}" +
                                "},"+
                                "\"format\":\"JSON\"," +
                                "\"fields\":\"file_id,file_name,cases.submitter_id,cases.case_id,data_category,data_type,cases.samples.tumor_descriptor,cases.samples.tissue_type,cases.samples.sample_type,cases.samples.submitter_id,cases.samples.sample_id,cases.samples.portions.analytes.aliquots.aliquot_id,cases.samples.portions.analytes.aliquots.submitter_id\"," +
                                "\"size\":\""+SIZE_LIMIT+"\"" +
                            "}";

            conn_str = conn_str + URLEncoder.encode(json_str, "UTF-8");
            System.err.println(conn_str);
            
            HttpURLConnection conn = (HttpURLConnection) (new URL(conn_str)).openConnection();
            conn.setRequestMethod("POST");
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