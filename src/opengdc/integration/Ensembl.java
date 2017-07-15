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
    private static HashMap<String, HashMap<String, String>> ensembl_data = new HashMap<>();
	
    private static void loadEnsemblTable() {
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
                    	entry.put("CHR", arr[0]);
                        entry.put("START", arr[3]);
                        entry.put("END", arr[4]);
                        entry.put("STRAND", arr[6]);
                        entry.put("TYPE", arr[2]);
                        String symbol = "NA";
                        String ensembl = "NA";
                        String[] extendedInfo_arr = arr[8].split(";");
                        for (String data: extendedInfo_arr) {
                            if (data.toLowerCase().trim().startsWith("gene_name")) {
                                //String[] name_split = data.split("="); // for gff3
                                String[] name_split = data.split("\""); // for gtf
                                symbol = name_split[name_split.length-1];
                            }
                            else if (data.toLowerCase().trim().startsWith("gene_id")) {
                            	//String[] id_split = data.split("="); // for gff3
                                String[] id_split = data.split("\""); //for gtf
                            	ensembl = id_split[id_split.length-1];
                            }
                        }
                        entry.put("SYMBOL", symbol);
                        if (!ensembl.equals("NA"))
                            ensembl_data.put(ensembl, entry);
                    }
                } catch (Exception e) { /* IF THROWS ERROR -> SKIP LINE */ }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
    public static HashMap<String, String> extractEnsemblInfo(String ensembl_id) {
    	if (ensembl_data.isEmpty()) 
    		loadEnsemblTable();
    	if (ensembl_data.containsKey(ensembl_id)) return ensembl_data.get(ensembl_id);
        return new HashMap<>();
    }
    
}
