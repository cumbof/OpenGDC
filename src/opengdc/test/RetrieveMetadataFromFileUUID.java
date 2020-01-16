package opengdc.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import opengdc.util.JSONUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author fabio
 */
public class RetrieveMetadataFromFileUUID {
    
    /*
    * https://docs.gdc.cancer.gov/API/Users_Guide/Search_and_Retrieval/#example-http-post-request
    */
    
    private static final int SIZE_LIMIT = 99999999;
    private static final String FILES_UUID = "\"0b50ee5b-3130-4401-84b1-630596632a12\", \"0c7752f2-252b-45a6-a088-70b1baaeda78\", \"3e8ee133-6bc5-41fc-83cb-ee51c5df4206\"";
    private static final String FIELDS = "\"file_id,file_name,cases.submitter_id,cases.case_id,data_category,data_type,cases.samples.tumor_descriptor,cases.samples.tissue_type,cases.samples.sample_type,cases.samples.submitter_id,cases.samples.sample_id,cases.samples.portions.analytes.aliquots.aliquot_id,cases.samples.portions.analytes.aliquots.submitter_id,experimental_strategy,platform,analysis.workflow_link,data_format,file_size\"";
    private static final String QUERY_OUT_FORMAT = "JSON"; // {JSON, TSV}
    private static final String OUT_PATH = "/Users/fabio/Downloads/tmp_query_file."+QUERY_OUT_FORMAT.toLowerCase();
    
    public static void main(String[] args) {
        try {
            String conn_str = "https://gdc-api.nci.nih.gov/files?from=1&size="+SIZE_LIMIT+"&pretty=true&fields="+FIELDS+"&format=JSON&filters=";
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
            
            FileOutputStream fos = new FileOutputStream(OUT_PATH);
            PrintStream out = new PrintStream(fos);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line; (line = reader.readLine()) != null;) {
                out.println(line);
                System.out.println(line);
            }
            System.out.println();
            reader.close();
            out.close();
            fos.close();
            
            conn.disconnect();
            
            File jsonFile = new File(OUT_PATH);
            URI uri = jsonFile.toURI();
            JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
            JSONObject root = new JSONObject(tokener);
            HashMap<String, Object> json_data = new HashMap<>(JSONUtils.jsonToMap(root));
            Object data_node_obj = json_data.get("data");
            
            HashMap<String, String> info = new HashMap<>();
            /*String aliquot_id = GDCQuery.searchFor("aliquot_id", data_node_obj, null);
            info.put("aliquot_id", aliquot_id!=null ? aliquot_id : "null");
            String experimental_strategy = GDCQuery.searchFor("experimental_strategy", data_node_obj, null);
            info.put("experimental_strategy", experimental_strategy!=null ? experimental_strategy : "null");
            String platform = GDCQuery.searchFor("platform", data_node_obj, null);
            info.put("platform", platform!=null ? platform : "null");
            String analysis_workflow_link = GDCQuery.searchFor("workflow_link", data_node_obj, null);
            info.put("workflow_link", analysis_workflow_link!=null ? analysis_workflow_link : "null");
            String data_category = GDCQuery.searchFor("data_category", data_node_obj, null);
            info.put("data_category", data_category!=null ? data_category : "null");
            String data_type = GDCQuery.searchFor("data_type", data_node_obj, null);
            info.put("data_type", data_type!=null ? data_type : "null");
            String data_format = GDCQuery.searchFor("data_format", data_node_obj, null);
            info.put("data_format", data_format!=null ? data_format : "null");
            String file_size = GDCQuery.searchFor("file_size", data_node_obj, null);
            info.put("file_size", file_size!=null ? file_size : "null");*/
            
            for (String attr: info.keySet())
                System.err.println(attr + ": " + info.get(attr));
            
            jsonFile.delete();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
}
