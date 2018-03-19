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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import javax.swing.JTextPane;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author fabio
 */
public class GDCQuery {
    
    /*
        Result window is too large, from + size must be less than or equal to: [10000] but was [2147483648]. 
        See the scroll api for a more efficient way to request large data sets. 
        This limit can be set by changing the [index.max_result_window] index level setting.
    */
    //private static final int SIZE_LIMIT = Integer.MAX_VALUE;
    private static final int SIZE_LIMIT = 10000; // 10000 starting from 0 -> 9999
    //private static final String FIELDS = "\"file_id,file_name,cases.submitter_id,cases.case_id,data_category,data_type,cases.samples.tumor_descriptor,cases.samples.tissue_type,cases.samples.sample_type,cases.samples.submitter_id,cases.samples.sample_id,cases.samples.portions.analytes.aliquots.aliquot_id,cases.samples.portions.analytes.aliquots.submitter_id\"";
    //private static final String BASE_SEARCH_URL = "https://gdc-api.nci.nih.gov/files?from=0&size="+SIZE_LIMIT+"&pretty=true&fields="+FIELDS+"&filters=";
    private static final String BASE_SEARCH_URL = "https://gdc-api.nci.nih.gov/files?from=0&size="+SIZE_LIMIT+"&pretty=true&filters=";
    private static final String BASE_DOWNLOAD_URL = "https://gdc-api.nci.nih.gov/data/";
    private static final int RECURSIVE_LIMIT = 100;
    private static final int BUFFER_SIZE = 4096;
    
    public static String query(String disease, String dataType, int recursive_iteration) {
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
            
            File outFile = new File(Settings.getTmpDir() + query_file_name);
            if (outFile.exists())
                outFile.delete();
            
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
            
            conn.disconnect();
            
            return Settings.getTmpDir() + query_file_name;
        }
        catch (Exception e) {
            recursive_iteration++;
            if (recursive_iteration < RECURSIVE_LIMIT)
                return query(disease, dataType, recursive_iteration++);
            else
                e.printStackTrace();
        }
        return null;
    }
    
    public static HashMap<String, ArrayList<Object>> searchFor(HashSet<String> keys, Object current_obj, HashMap<String, ArrayList<Object>> results) {
        if (results == null)
            results = new HashMap<>();
        
        if (current_obj instanceof HashMap) {
            HashMap<String, Object> hashmap = (HashMap<String, Object>)current_obj;
            for (String k: hashmap.keySet()) {
                if (keys.contains(k.toLowerCase().trim())) {
                    HashMap<String, Object> reduced_hashmap = new HashMap<>();
                    for (String key: hashmap.keySet()) {
                        if (!(hashmap.get(key) instanceof HashMap) && !(hashmap.get(key) instanceof List))
                            reduced_hashmap.put(key, hashmap.get(key));
                    }
                    ArrayList<Object> val = new ArrayList<>();
                    if (results.containsKey(k.toLowerCase().trim()))
                        val = results.get(k.toLowerCase().trim());
                    val.add(reduced_hashmap);
                    results.put(k.toLowerCase().trim(), val);
                }
                else {
                    if ((hashmap.get(k) instanceof HashMap) || (hashmap.get(k) instanceof List))
                        results.putAll(searchFor(keys, hashmap.get(k), results));
                }
                
            }
        }
        else if (current_obj instanceof List) {
            ArrayList<Object> list = (ArrayList<Object>)current_obj;
            for (Object o: list) {
                if ((o instanceof HashMap) || (o instanceof List))
                    results.putAll(searchFor(keys, o, results));
            }
        }
        return results;
    }
    
    public static HashMap<String, HashMap<String, String>> extractInfo(String last_query_file_tmp_path) {
        //aliquot = "";
        HashMap<String, HashMap<String, String>> data = new HashMap<>();
        File jsonFile = new File(last_query_file_tmp_path);
        try {    
            URI uri = jsonFile.toURI();
            InputStream in = uri.toURL().openStream();
            JSONTokener tokener = new JSONTokener(in);
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
            in.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        //jsonFile.delete();
        return data;
    }
    
    public static void downloadFile(String uuid, String outFolderPath, String fileName, boolean requestRelated, int recursive_iteration, JTextPane logPane) {
        try {
            String url_string = BASE_DOWNLOAD_URL + uuid;
            if (requestRelated)
                url_string += "?related_files=true"; // why it does not always work?
            //System.err.println(url);
            System.err.println(uuid + "\t" + fileName);
            GUI.appendLog(logPane, uuid + "\t" + fileName + "\n");

            File outFile = new File(outFolderPath + fileName);
            if (outFile.exists())
                outFile.delete();
            
            /*URL url = new URL(url_string);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            InputStream is = httpConn.getInputStream();
            FileOutputStream fos = new FileOutputStream(outFolderPath + fileName);
            int bytesRead;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = is.read(buffer)) != -1)
                fos.write(buffer, 0, bytesRead);
            fos.close();
            is.close();
            httpConn.disconnect();*/
                        
            if (fileName.trim().toLowerCase().endsWith(".gz") || fileName.trim().toLowerCase().endsWith(".xlsx")) {
                URL url = new URL(url_string);
                ReadableByteChannel rbc = Channels.newChannel(url.openStream());
                FileOutputStream fos = new FileOutputStream(outFolderPath + fileName);
                long read, pos = 0;
                while ((read = fos.getChannel().transferFrom(rbc, pos, Long.MAX_VALUE)) > 0)
                    pos += read;
                fos.close();
                rbc.close();
            }
            else {
                URL url = new URL(url_string);
                URLConnection connection = url.openConnection();
                FileOutputStream fos = new FileOutputStream(outFolderPath + fileName);
                PrintStream out = new PrintStream(fos);
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = reader.readLine();
                while (line != null) {
                    String nextLine = reader.readLine();
                    if (nextLine != null)
                        out.println(line);
                    else
                        out.print(line); //remove last newline char
                    line = nextLine;
                }
                reader.close();
                out.close();
                fos.close();
            }
        }
        catch (Exception e) {
            recursive_iteration++;
            if (recursive_iteration < RECURSIVE_LIMIT)
                downloadFile(uuid, outFolderPath, fileName, requestRelated, recursive_iteration, logPane);
            else
                e.printStackTrace();
        }
    }
    
    /*
        field: 
            - files.file_id
            - cases.samples.portions.analytes.aliquots.aliquot_id
    */
    // endpoint: {files, cases}
    public static ArrayList<HashMap<String, ArrayList<Object>>> retrieveExpInfoFromAttribute(String endpoint, String field, String value, HashSet<String> attributes, int recursive_iteration, int from, ArrayList<HashMap<String, ArrayList<Object>>> info) {
        Date now = new Date();
        SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
        String query_file_name = "query_"+ft.format(now)+".json";
        String query_file_path = Settings.getTmpDir() + query_file_name;
        if (attributes!=null) {
            if (!attributes.isEmpty()) {
                try {
                    if (info == null)
                        info = new ArrayList<>();
                    //String fields = "\"file_id,file_name,cases.submitter_id,cases.case_id,data_category,data_type,cases.samples.tumor_descriptor,cases.samples.tissue_type,cases.samples.sample_type,cases.samples.submitter_id,cases.samples.sample_id,cases.samples.portions.analytes.aliquots.aliquot_id,cases.samples.portions.analytes.aliquots.submitter_id,experimental_strategy,platform,analysis.workflow_link,data_format,file_size\"";
                    //String fields = "\"";
                    String fields = "\",";
                    for (String attr: attributes)
                        fields += attr + ",";
                    //fields = fields.substring(0, fields.length()-1) + "\"";
                    
                    String conn_str = "https://gdc-api.nci.nih.gov/"+endpoint+"?from="+from+"&size="+SIZE_LIMIT+"&pretty=true&fields="+fields+"&format=JSON&filters=";
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
                    
                    FileOutputStream fos = new FileOutputStream(query_file_path);
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
                    
                    conn.disconnect();

                    File jsonFile = new File(query_file_path);
                    URI uri = jsonFile.toURI();
                    InputStream in = uri.toURL().openStream();
                    JSONTokener tokener = new JSONTokener(in);
                    JSONObject root = new JSONObject(tokener);
                    HashMap<String, Object> json_data = new HashMap<>(JSONUtils.jsonToMap(root));
                    
                    Object data_node_obj = json_data.get("data");
                    HashMap<String, Object> root_node = (HashMap<String, Object>)data_node_obj;
                    ArrayList<Object> hits_node = (ArrayList<Object>)root_node.get("hits");
                    
                    HashMap<String, Object> pagination_node = (HashMap<String, Object>)root_node.get("pagination");
                    int total = (int)pagination_node.get("total");
                    //System.err.println("pagination.total: "+total);
                    
                    for (Object node: hits_node) {
                        HashMap<String, ArrayList<Object>> data_node = new HashMap<>();
                        HashSet<String> keys = new HashSet<>();
                        for (String attribute: attributes) {
                            String[] attribute_split = attribute.split("\\.");
                            String searchForKey = attribute_split[attribute_split.length-1];
                            keys.add(searchForKey);
                        }
                        HashMap<String, ArrayList<Object>> values = searchFor(keys, node, null);
                        for (String attr: values.keySet())
                            data_node.put(attr, values.get(attr));
                        info.add(data_node);
                    }
                    
                    in.close();
                    //jsonFile.delete();
                    
                    if ((total - ((recursive_iteration+1)*SIZE_LIMIT)) > 0) {
                        from = ((recursive_iteration+1)*SIZE_LIMIT)+1;
                        recursive_iteration++;
                        //System.err.println(from);
                        return retrieveExpInfoFromAttribute(endpoint, field, value, attributes, recursive_iteration, from, info);
                    }
                    else return info;
                }
                catch (Exception e) {
                    recursive_iteration++;
                    if (recursive_iteration < RECURSIVE_LIMIT)
                        return retrieveExpInfoFromAttribute(endpoint, field, value, attributes, recursive_iteration, from, info);
                    else {
                        e.printStackTrace();
                        return new ArrayList<>();
                    }
                }
            }
        }
        return new ArrayList<>();
    }
    
    //public static void main(String[] args) {
        /*HashMap<String, Boolean> attributes = new HashMap<>();
        attributes.put("data_category", false);
        attributes.put("data_format", false);
        attributes.put("data_type", true);
        attributes.put("experimental_strategy", false);
        attributes.put("file_id", true);
        attributes.put("file_name", false);
        attributes.put("file_size", false);
        attributes.put("platform", false);
        attributes.put("analysis.analysis_id", false);
        attributes.put("analysis.workflow_link", false);
        attributes.put("analysis.workflow_type", false);
        attributes.put("cases.case_id", false);
        attributes.put("cases.disease_type", true);
        attributes.put("cases.primary_site", false);
        attributes.put("cases.demographic.year_of_birth", false);
        attributes.put("cases.project.program.program_id", false);
        attributes.put("cases.project.program.name", false);
        attributes.put("cases.submitter_id", false);
        attributes.put("cases.samples.tumor_descriptor", false);
        attributes.put("cases.samples.tissue_type", false);
        
        String aliquot_uuid = "0aac83d9-dfb0-4aa8-9fc4-b12acef7fdee";
        HashMap<String, String> data = retrieveExpInfoFromAttribute("cases.samples.portions.analytes.aliquots.aliquot_id", aliquot_uuid.toLowerCase(), new HashSet<>(attributes.keySet()));
        for (String attr: data.keySet())
            System.err.println(attr + "\t" + data.get(attr));*/
        
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
