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
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class RetrieveManuallyCuratedDataTypes {
    
    public static void main(String[] args) {
        HashMap<String, HashSet<String>> dataType2experimentalStrategy = new HashMap<>();
        
        String meta_folder_path = "/Users/fabio/Downloads/test_gdc_download/TCGA/TCGA-BRCA/meta/";
        for (String meta_file_name: (new File(meta_folder_path)).list()) {
            String meta_file_path = meta_folder_path+meta_file_name;
            HashMap<String, String> meta = readMeta(meta_file_path);
            if (meta.containsKey("manually_curated__data_type")) {
                String dataType = meta.get("manually_curated__data_type");
                String expStr = "";
                if (meta.containsKey("manually_curated__experimental_strategy"))
                    expStr = meta.get("manually_curated__experimental_strategy");
                if (!expStr.trim().equals("")) {
                    HashSet<String> expStrs = new HashSet<>();
                    if (dataType2experimentalStrategy.containsKey(dataType))
                        expStrs = dataType2experimentalStrategy.get(dataType);
                    expStrs.add(expStr);
                    dataType2experimentalStrategy.put(dataType, expStrs);
                }
            }
        }
        
        for (String dt: dataType2experimentalStrategy.keySet()) {
            System.err.print(dt+":\t");
            for (String es: dataType2experimentalStrategy.get(dt))
                System.err.print(es+"\t");
            System.err.println();
        }
    }

    private static HashMap<String, String> readMeta(String meta_file_path) {
        HashMap<String, String> data = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(meta_file_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.trim().equals("")) {
                        String[] line_split = line.split("\t");
                        data.put(line_split[0], line_split[1]);
                    }
                }
                catch (Exception e) {}
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }
    
}
