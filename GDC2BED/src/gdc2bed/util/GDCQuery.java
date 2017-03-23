/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gdc2bed.util;

import gdc2bed.GUI;
import gdc2bed.Settings;
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
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author fabio
 */
public class GDCQuery {
    
    private static final int SIZE_LIMIT = Integer.MAX_VALUE;
    private static final String BASE_SEARCH_URL = "https://gdc-api.nci.nih.gov/files?from=1&size="+SIZE_LIMIT+"&pretty=true&filters=";
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
            for (String line; (line = reader.readLine()) != null;)
                out.println(line);
            reader.close();
            out.close();
            fos.close();
            
            last_query_file_path = Settings.getTmpDir() + query_file_name;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static HashMap<String, HashMap<String, String>> extractInfo(String last_query_file_tmp_path) {
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
            for (Object map_obj: hits_node) {
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
                data.put(key, values);
            }
        }
        catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return data;
    }
    
    /** TO TEST **/
    /*public static void downloadFiles(HashSet<String> UUIDs, String outFolderPath) {
        try {
            System.err.println("UUIDs size: " + UUIDs.size());
            String UUID_list = "";
            for (String UUID: UUIDs)
                UUID_list += "\""+UUID+"\",";
            UUID_list = UUID_list.substring(0, UUID_list.length()-1);
            System.err.println(UUID_list);
            
            String json_str = "{" +
                                  "\"ids\":[" +
                                      UUID_list +
                                  "]" +
                              "}";
            
            String conn_str = BASE_DOWNLOAD_URL + URLEncoder.encode(json_str, "UTF-8");         
            
            Date now = new Date();
            SimpleDateFormat ft = new SimpleDateFormat("yyyyMMddHHmmss");
            String query_file_name = "gdc_"+ft.format(now)+".tar.gz";
            
            URL website = new URL(conn_str);
            ReadableByteChannel rbc = Channels.newChannel(website.openStream());
            FileOutputStream fos = new FileOutputStream(outFolderPath + query_file_name);
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fos.close();
            rbc.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    public static void downloadFile(String uuid, String outFolderPath, String fileName) {
        try {
            String url = BASE_DOWNLOAD_URL + uuid + "?related_files=true";
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
    
    /*public static void main(String[] args) {
        String uuid = "8751a889-cb3e-4487-ba6f-ac91651666e7";
        String outFolderPath = "/Users/fabio/Downloads/test_gdc_download/data/";
        String fileName = "TCGA.BRCA.muse.8751a889-cb3e-4487-ba6f-ac91651666e7.somatic.maf.gz";
        downloadFile(uuid, outFolderPath, fileName);
    }*/
    
}
