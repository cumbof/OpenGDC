/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author fabio
 */
public class DetectRedundantMetadata {
    
    public static final String META_DIR_PATH = "/Users/fabio/Desktop/ov_meta/";
    public static final String REDUNDANT_META_FILE_PATH = "/Users/fabio/Desktop/redundant_attributes.tsv";
    
    public static void main(String args[]) {
        HashMap<String, HashMap<String, String>> redundantValues = new HashMap<>();
        for (File metafile: (new File(META_DIR_PATH)).listFiles()) {
            if (metafile.isFile() && metafile.getName().toLowerCase().endsWith("meta")) {
                redundantValues = new HashMap<>();
                redundantValues = detectRedundantMetadata(metafile, redundantValues);
                //redundantValues = removeEmptyAttributes(redundantValues);
                //printData(metafile, redundantValues);
            }
        }
        redundantValues = removeEmptyAttributes(redundantValues);
        printData(REDUNDANT_META_FILE_PATH, redundantValues);
    }

    //private static HashMap<String, HashMap<String, String>> detectRedundantMetadata(File metafile) {
    private static HashMap<String, HashMap<String, String>> detectRedundantMetadata(File metafile, HashMap<String, HashMap<String, String>> redundantValues) {
        //HashMap<String, HashMap<String, String>> redundantValues = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(metafile.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] line_split = line.split("\t");
                    String attribute = line_split[0];
                    String value = line_split[1].toLowerCase();
                    String[] attribute_split = line_split[0].split("__");
                    String stripped_attribute = attribute_split[attribute_split.length-1];
                    HashMap<String, String> attr_list = new HashMap<>();
                    if (redundantValues.containsKey(stripped_attribute)) {
                        attr_list = redundantValues.get(stripped_attribute);
                        for (String attr: attr_list.keySet()) {
                            if (attr_list.get(attr).equals(value)) {
                                attr_list.put(attribute, value);
                                break;
                            }
                        }
                    }
                    else
                        attr_list.put(attribute, value);
                    redundantValues.put(stripped_attribute, attr_list);
                }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) { }
        return redundantValues;
    }
    
    private static HashMap<String, HashMap<String, String>> removeEmptyAttributes(HashMap<String, HashMap<String, String>> redundantValues) {
        HashMap<String, HashMap<String, String>> redundantValuesMod = new HashMap<>();
        for (String stripped_attribute: redundantValues.keySet()) {
            if (redundantValues.get(stripped_attribute).size() > 1)
                redundantValuesMod.put(stripped_attribute, redundantValues.get(stripped_attribute));
        }
        return redundantValuesMod;
    }

    //private static void printData(File metafile, HashMap<String, HashMap<String, String>> redundantValues) {
    private static void printData(String redundant_meta_file_path, HashMap<String, HashMap<String, String>> redundantValues) {
        try {
            Files.write((new File(redundant_meta_file_path)).toPath(), ("").getBytes("UTF-8"), StandardOpenOption.CREATE);
            //System.err.println(metafile.getName());
            ArrayList<String> sortedKeys = new ArrayList<>(redundantValues.keySet());
            Collections.sort(sortedKeys);
            for (String stripped_attribute: sortedKeys) {
                //System.err.println("\t" + stripped_attribute);
                //System.err.println(stripped_attribute);
                Files.write((new File(redundant_meta_file_path)).toPath(), (stripped_attribute + "\t\n").getBytes("UTF-8"), StandardOpenOption.APPEND);
                ArrayList<String> sortedAttributes = new ArrayList<>(redundantValues.get(stripped_attribute).keySet());
                Collections.sort(sortedAttributes);
                for (String attribute: sortedAttributes) {
                    //System.err.println("\t\t" + attribute + "\t" + redundantValues.get(stripped_attribute).get(attribute));
                    //System.err.println("\t" + attribute + "\t" + redundantValues.get(stripped_attribute).get(attribute));
                    Files.write((new File(redundant_meta_file_path)).toPath(), (attribute + "\t" + redundantValues.get(stripped_attribute).get(attribute) + "\n").getBytes("UTF-8"), StandardOpenOption.APPEND);
                }
            }
            Files.write((new File(redundant_meta_file_path)).toPath(), ("\n").getBytes("UTF-8"), StandardOpenOption.APPEND);
        }
        catch (Exception e) { }
    }

}
