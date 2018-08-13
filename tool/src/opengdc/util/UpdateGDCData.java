/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import org.apache.commons.io.FileUtils;
import opengdc.Settings;

/**
 *
 * @author fabio
 */
public class UpdateGDCData {
    
    public static String getUpdateTableAttribute(String program, String tumor, String data_type, URL filesinfo_url, String key, String attribute, boolean isOriginal) {
        String res = "null";
        try {
            String tmp_dir = Settings.getTmpDir();
            if (!tmp_dir.endsWith("/"))
                tmp_dir += "/";
            String local_file_path = tmp_dir + program + "_" + tumor + "_" + data_type + "_" + String.valueOf(isOriginal) + "_" + Settings.getUpdateTableName();
            File local_file = new File(local_file_path);
            if (!local_file.exists())
                FileUtils.copyURLToFile(filesinfo_url, local_file);
            HashMap<String, HashMap<String, String>> updatetable;
            if (isOriginal)
                updatetable = loadUpdateTable_original(local_file.getAbsolutePath());
            else
                updatetable = loadUpdateTable_converted(local_file.getAbsolutePath());
            res = updatetable.get(key).get(attribute);
        }
        catch (Exception e) {
            return res;
        }
        return res;
    }

    public static HashMap<String, HashMap<String, String>> loadUpdateTable_original(String updatetable_path) {
        HashMap<String, HashMap<String, String>> updatetable = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(updatetable_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.equals("")) {
                    String[] line_split = line.split("\t");
                    HashMap<String, String> updateInfo = new HashMap<>();
                    updateInfo.put("file_id", line_split[0]);
                    updateInfo.put("file_name", line_split[1]);
                    updateInfo.put("file_size", line_split[2]);
                    updateInfo.put("md5sum", line_split[3]);
                    updateInfo.put("updated_datetime", line_split[4]);
                    updateInfo.put("downloaded_datetime", line_split[5]);
                    updatetable.put(line_split[0], updateInfo);
                }
            }
            br.close();
            in.close();
            fstream.close();
        }
        catch (Exception e) {
            return new HashMap<>();
        }
        return updatetable;
    }
    
    public static HashMap<String, HashMap<String, String>> loadUpdateTable_converted(String updatetable_path) {
        HashMap<String, HashMap<String, String>> updatetable = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(updatetable_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.equals("")) {
                    String[] line_split = line.split("\t");
                    HashMap<String, String> updateInfo = new HashMap<>();
                    updateInfo.put("aliquot_uuid", line_split[0]);
                    updateInfo.put("file_uuid", line_split[1]);
                    updateInfo.put("converted_timestamp", line_split[2]);
                    updateInfo.put("md5sum", line_split[3]);
                    updateInfo.put("file_size", line_split[4]);
                    updatetable.put(line_split[0], updateInfo);
                }
            }
            br.close();
            in.close();
            fstream.close();
        }
        catch (Exception e) {
            return new HashMap<>();
        }
        return updatetable;
    }
    
}