/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.integration;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import opengdc.Settings;

/**
 *
 * @author fabio
 */
public class Ensembl {
        
    public static HashMap<String, String> extractEnsemblInfo(String ensembl_id) {
        HashMap<String, String> result = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(Settings.getENSEMBLDataPath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("#")) {
                        String[] arr = line.split("\t");
                        String extendedInfo = arr[8];
                        if (extendedInfo.contains(ensembl_id)) {
                            result.put("CHR", arr[0]);
                            result.put("START", arr[3]);
                            result.put("END", arr[4]);
                            result.put("STRAND", arr[6]);
                            result.put("TYPE", arr[2]);
                            String symbol = "NA";
                            String[] extendedInfo_arr = extendedInfo.split(";");
                            for (String data: extendedInfo_arr) {
                                if (data.toLowerCase().trim().startsWith("gene_name")) {
                                    String[] name_split = data.split("\"");
                                    symbol = name_split[name_split.length-1];
                                }
                            }
                            result.put("SYMBOL", symbol);
                            break;
                        }
                    }
                } catch (Exception e) {}
            }
            br.close();
            in.close();
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
    
}
