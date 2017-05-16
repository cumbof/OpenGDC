/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
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
            String conn_str = "https://gdc-api.nci.nih.gov/files?from=1&size="+SIZE_LIMIT+"&pretty=true&fields="+FIELDS+"&format=TSV&filters=";
            String json_str = "{" +
                                    "\"op\":\"in\"," +
                                    "\"content\":{" +
                                        "\"field\":\"files.file_id\"," +
                                        "\"value\":[" +
                                            FILES_UUID +
                                        "]" +
                                    "}" +
                                "}";
            
            conn_str += URLEncoder.encode(json_str, "UTF-8");
            System.err.println(conn_str);
            HttpURLConnection conn = (HttpURLConnection) (new URL(conn_str)).openConnection();

            /*conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");*/
            
            conn.connect();
            
            /*OutputStream os = conn.getOutputStream();
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os));
            pw.write(URLEncoder.encode(json_str, "UTF-8"));
            pw.close();*/
                        
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
