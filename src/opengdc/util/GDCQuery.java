/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
 */
package opengdc.util;

import opengdc.GUI;
import opengdc.Settings;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author fabio
 */
public class GDCQuery {
    
    
    /*
        Result window is too large, from + size must be less than or equal to: [100000000] but was [2147483648]. 
        See the scroll api for a more efficient way to request large data sets. 
        This limit can be set by changing the [index.max_result_window] index level setting.
    */
    //private static final int SIZE_LIMIT = Integer.MAX_VALUE;
    private static final int SIZE_LIMIT = 99999999; // 100000000 starting from 0 -> 99999999
    //private static final String FIELDS = "\"file_id,file_name,cases.submitter_id,cases.case_id,data_category,data_type,cases.samples.tumor_descriptor,cases.samples.tissue_type,cases.samples.sample_type,cases.samples.submitter_id,cases.samples.sample_id,cases.samples.portions.analytes.aliquots.aliquot_id,cases.samples.portions.analytes.aliquots.submitter_id\"";
    //private static final String BASE_SEARCH_URL = "https://gdc-api.nci.nih.gov/files?from=0&size="+SIZE_LIMIT+"&pretty=true&fields="+FIELDS+"&filters=";
    private static final String BASE_SEARCH_URL = "https://gdc-api.nci.nih.gov/files?from=0&size="+SIZE_LIMIT+"&pretty=true&filters=";
    private static final String BASE_DOWNLOAD_URL = "https://gdc-api.nci.nih.gov/data/";
    private static String last_query_file_path = "NA";
    
    public static String getLastQueryFilePath() {
        return last_query_file_path;
    }
    
    public static void query(String disease, String dataType) {
        try {
            String json_str = "{" +
                                  "\"op\":\"and\"," +
                                  "\"content\":[" +
                                      "{" +
                                          "\"op\":\"=\"," +
                                          "\"content\":{" +
                                              "\"field\":\"cases.project.project_id\"," +
                                              "\"value\":[" +
                                                  "\""+disease+"\"" +
                                              "]" + 
                                          "}" +
                                      "}," +
                                      "{" +
                                          "\"op\":\"=\"," +
                                          "\"content\":{" +
                                              "\"field\":\"files.data_type\"," +
                                              "\"value\":[" +
                                                  "\""+dataType+"\"" +
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
            
            String conn_str = BASE_SEARCH_URL + URLEncoder.encode(json_str, "UTF-8");            
            HttpURLConnection conn = (HttpURLConnection) (new URL(conn_str)).openConnection();
            conn.setRequestProperty("Content-Type", "application/json");
            
            Date now = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
            
            String query_file_name = "query_"+ft.format(now)+".json";
            FileOutputStream fos = new FileOutputStream(Settings.getTmpDir() + query_file_name);
            PrintStream out = new PrintStream(fos);
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            for (String line; (line = reader.readLine()) != null;) {
                //System.err.println(line);
                out.println(line);
            }
            reader.close();
            out.close();
            fos.close();
            
            last_query_file_path = Settings.getTmpDir() + query_file_name;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static String searchFor(String key, Object obj) {
        if (obj instanceof HashMap) {
            HashMap<String, Object> hashmap = (HashMap<String, Object>)obj;
            HashSet<String> tmps = new HashSet<>();
            for (String k: hashmap.keySet()) {
                if (k.toLowerCase().trim().equals(key)) {
                    //return (String)hashmap.get(k);
                    return String.valueOf(hashmap.get(k));
                }
                else
                    tmps.add(searchFor(key, hashmap.get(k)));
            }
            for (String tmp: tmps) {
                if (tmp != null)
                    return tmp;
            }
        }
        else if (obj instanceof List) {
            ArrayList<Object> list = (ArrayList<Object>)obj;
            HashSet<String> tmps = new HashSet<>();
            for (Object o: list)
                tmps.add(searchFor(key, o));
            for (String tmp: tmps) {
                if (tmp != null)
                    return tmp;
            }
        }
        return null;
    }
    
    public static HashMap<String, HashMap<String, String>> extractInfo(String last_query_file_tmp_path) {
        //aliquot = "";
        HashMap<String, HashMap<String, String>> data = new HashMap<>();
        try {
            File jsonFile = new File(last_query_file_tmp_path);
            URI uri = jsonFile.toURI();
            JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
            JSONObject root = new JSONObject(tokener);
            HashMap<String, Object> json_data = new HashMap<>(JSONUtils.jsonToMap(root));
            
            Object data_node_obj = json_data.get("data");
            HashMap<String, Object> data_node = (HashMap<String, Object>)data_node_obj;
            ArrayList<Object> hits_node = (ArrayList<Object>)data_node.get("hits");
            //System.err.println("NODES: " + hits_node.size());
            //int key_count = 0;
            for (Object map_obj: hits_node) {
                //aliquot = "";
                HashMap<String, Object> map = (HashMap<String, Object>)map_obj;
                String key = "";
                HashMap<String, String> values = new HashMap<>();
                for (String k: map.keySet()) {
                    if (k.toLowerCase().trim().equals("file_id"))
                        key = (String)map.get(k);
                    else {
                        if (map.get(k) instanceof String)
                            values.put(k, (String)map.get(k));
                    }
                }
                /*if (key.equals("")) {
                    searchForAliquotUUID(map_obj);
                    key = key_count + "_" + aliquot;
                    if (key.equals(""))
                        key = String.valueOf(key_count);
                    key_count++;
                }*/
                /*if (data.containsKey(key))
                    System.err.println("exp already exists");*/
                data.put(key, values);
            }
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    public static void downloadFile(String uuid, String outFolderPath, String fileName, boolean requestRelated) {
        try {
            String url = BASE_DOWNLOAD_URL + uuid;
            if (requestRelated)
                url += "?related_files=true"; // why it does not always work?
            //System.err.println(url);
            System.err.println(uuid + "\t" + fileName);
            GUI.appendLog(uuid + "\t" + fileName + "\n");
            
            URL website = new URL(url);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(outFolderPath + fileName);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /*
        field: 
            - files.file_id
            - cases.samples.portions.analytes.aliquots.aliquot_id
    */
    public static HashMap<String, String> retrieveExpInfoFromAttribute(String field, String value, HashSet<String> attributes) {
        if (attributes!=null) {
            if (!attributes.isEmpty()) {
                try {
                    HashMap<String, String> info = new HashMap<>();
                    //String fields = "\"file_id,file_name,cases.submitter_id,cases.case_id,data_category,data_type,cases.samples.tumor_descriptor,cases.samples.tissue_type,cases.samples.sample_type,cases.samples.submitter_id,cases.samples.sample_id,cases.samples.portions.analytes.aliquots.aliquot_id,cases.samples.portions.analytes.aliquots.submitter_id,experimental_strategy,platform,analysis.workflow_link,data_format,file_size\"";
                    //String fields = "\"";
                    String fields = "\",";
                    for (String attr: attributes)
                        fields += attr + ",";
                    //fields = fields.substring(0, fields.length()-1) + "\"";
                    
                    String conn_str = "https://gdc-api.nci.nih.gov/files?from=0&size="+SIZE_LIMIT+"&pretty=true&fields="+fields+"&format=JSON&filters=";
                    String json_str = "{" +
                                            "\"op\":\"in\"," +
                                            "\"content\":{" +
                                                "\"field\":\""+field+"\"," +
                                                "\"value\":[" +
                                                    "\""+value+"\"" +
                                                "]" +
                                            "}" +
                                        "}";

                    conn_str += URLEncoder.encode(json_str, "UTF-8");
                    System.err.println(conn_str);
                    HttpURLConnection conn = (HttpURLConnection) (new URL(conn_str)).openConnection();
                    conn.connect();

                    Date now = new Date();
                    SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");

                    String query_file_name = "query_"+ft.format(now)+".json";
                    FileOutputStream fos = new FileOutputStream(Settings.getTmpDir() + query_file_name);
                    PrintStream out = new PrintStream(fos);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    for (String line; (line = reader.readLine()) != null;) {
                        //System.err.println(line);
                        out.println(line);
                    }
                    //System.err.println("----------------\n\n");
                    reader.close();
                    out.close();
                    fos.close();

                    File jsonFile = new File(Settings.getTmpDir() + query_file_name);
                    URI uri = jsonFile.toURI();
                    JSONTokener tokener = new JSONTokener(uri.toURL().openStream());
                    JSONObject root = new JSONObject(tokener);
                    HashMap<String, Object> json_data = new HashMap<>(JSONUtils.jsonToMap(root));
                    Object data_node_obj = json_data.get("data");

                    for (String attribute: attributes) {
                        String[] attribute_split = attribute.split("\\.");
                        String searchForKey = attribute_split[attribute_split.length-1];
                        String val = searchFor(searchForKey, data_node_obj);
                        info.put(attribute, val!=null ? val : "");
                    }

                    conn.disconnect();
                    return info;
                }
                catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
        return null;
    }
    
    //public static void main(String[] args) {
        /*String file_uuid = "0b50ee5b-3130-4401-84b1-630596632a12";
        System.err.println(retrieveAliquotFromFileUUID(file_uuid));*/
        
        /*String disease = "TCGA-ACC";
        String dataType = "Gene Expression Quantification";
        query(disease, dataType);
        HashMap<String, HashMap<String, String>> info = extractInfo(last_query_file_path);
        for (String k: info.keySet()) {
            System.err.println(k);
            for (String v: info.get(k).keySet())
                System.err.println("\t"+v+"\t"+info.get(k).get(v));
        }
        System.err.println("\nsize: "+info.size());*/
        
        /*String uuid = "8751a889-cb3e-4487-ba6f-ac91651666e7";
        String outFolderPath = "/Users/fabio/Downloads/test_gdc_download/data/";
        String fileName = "TCGA.BRCA.muse.8751a889-cb3e-4487-ba6f-ac91651666e7.somatic.maf.gz";
        downloadFile(uuid, outFolderPath, fileName, false);*/
    //}
    
}
