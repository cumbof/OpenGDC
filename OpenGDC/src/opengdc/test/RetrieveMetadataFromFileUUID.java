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
    private static final String FILES_UUID = "\"0b50ee5b-3130-4401-84b1-630596632a12\", \"0c7752f2-252b-45a6-a088-70b1baaeda78\", \"3e8ee133-6bc5-41fc-83cb-ee51c5df4206\"";
    private static final String FIELDS = "\"file_id,file_name,cases.submitter_id,cases.case_id,data_category,data_type,cases.samples.tumor_descriptor,cases.samples.tissue_type,cases.samples.sample_type,cases.samples.submitter_id,cases.samples.sample_id,cases.samples.portions.analytes.aliquots.aliquot_id,cases.samples.portions.analytes.aliquots.submitter_id\"";
    
    public static void main(String[] args) {
        try {
<<<<<<< HEAD
            String conn_str = "https://gdc-api.nci.nih.gov/files?from=1&size="+SIZE_LIMIT+"&pretty=true&fields="+FIELDS+"&format=TSV&filters=";
=======
            String conn_str = "https://gdc-api.nci.nih.gov/files?from=1&pretty=true&";
>>>>>>> origin/master
            String json_str = "{" +
                                    "\"op\":\"in\"," +
                                    "\"content\":{" +
                                        "\"field\":\"files.file_id\"," +
                                        "\"value\":[" +
                                            FILES_UUID +
                                        "]" +
                                    "}" +
<<<<<<< HEAD
                                "}";
            
            conn_str += URLEncoder.encode(json_str, "UTF-8");
=======
                                "},"+
                                "\"format\":\"JSON\"," +
                                "\"fields\":\"file_id,file_name,cases.submitter_id,cases.case_id,data_category,data_type,cases.samples.tumor_descriptor,cases.samples.tissue_type,cases.samples.sample_type,cases.samples.submitter_id,cases.samples.sample_id,cases.samples.portions.analytes.aliquots.aliquot_id,cases.samples.portions.analytes.aliquots.submitter_id\"," +
                                "\"size\":\""+SIZE_LIMIT+"\"" +
                            "}";

            conn_str = conn_str + URLEncoder.encode(json_str, "UTF-8");
>>>>>>> origin/master
            System.err.println(conn_str);
            HttpURLConnection conn = (HttpURLConnection) (new URL(conn_str)).openConnection();
<<<<<<< HEAD

            /*conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");*/
            
            conn.connect();
            
            /*OutputStream os = conn.getOutputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
            pw.write(URLEncoder.encode(json_str, "UTF-8"));
            pw.close();*/
                        
=======
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
>>>>>>> origin/master
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line; (line = reader.readLine()) != null;)
                System.out.println(line);
            reader.close();
            
            conn.disconnect();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}