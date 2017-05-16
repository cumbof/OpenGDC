/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
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
    
    private static String ensembl_table_path = Settings.getENSEMBLDataPath();
        
    public static HashMap<String, String> extractEnsemblInfo(String ensembl_id) {
        HashMap<String, String> result = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(ensembl_table_path);
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
                                if (data.toLowerCase().trim().startsWith("name")) {
                                    String[] name_split = data.split("=");
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
