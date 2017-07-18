/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import opengdc.Settings;

/**
 *
 * @author fabio
 */
public class RetrieveTypesFromEnsemblTable {
    
    public static void main(String[] args) {
        String ensembl_table_path = Settings.getENSEMBLDataPath();
        HashMap<String, Integer> type2count = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(ensembl_table_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("#") && !line.trim().equals("")) {
                    	HashMap<String, String> entry = new HashMap<>();
                    	String[] arr = line.split("\t");
                        String line_type = arr[2].trim().toLowerCase();
                        int count = 1;
                        if (type2count.containsKey(line_type))
                            count = count + type2count.get(line_type);
                        type2count.put(line_type, count);
                    }
                }
                catch (Exception e) {}
            }
        }
        catch (Exception e) {}
        
        //print
        for (String type: type2count.keySet())
            System.err.println(type + " : " + type2count.get(type));
    }
    
}
