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
public class GeneNames {
    
    private static String genenames_table_path = Settings.getGENENAMESDataPath();
    private static HashMap<String, String> symbol2entrez = new HashMap<>();
    private static HashMap<String, String> mirnaid2entrez = new HashMap<>();
    
    public static HashMap<String, String> getSymbol2Entrez() {
        if (symbol2entrez.isEmpty()) {
            try {
                boolean firstLine = true; // just to skip the first line (header)
                InputStream fstream = new FileInputStream(genenames_table_path);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        if (!firstLine) {
                            String[] arr = line.split("\t");
                            String symbol = arr[1];
                            String entrez = arr[18];
                            symbol2entrez.put(symbol, entrez);
                            
                            String[] synonyms = arr[10].replaceAll("\"", "").split("\\|");
                            for (String syn: synonyms)
                                symbol2entrez.put(syn, entrez);
                        }
                        else
                            firstLine = false;
                    } catch (Exception e) {}
                }
                br.close();
                in.close();
                fstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return symbol2entrez;
    }
    
    public static HashMap<String, String> getMirnaID2Entrez() {
        if (mirnaid2entrez.isEmpty()) {
            try {
                boolean firstLine = true; // just to skip the first line (header)
                InputStream fstream = new FileInputStream(genenames_table_path);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        if (!firstLine) {
                            String[] arr = line.split("\t");
                            String mirnaid = arr[8];
                            String entrez = arr[18];
                            mirnaid2entrez.put(mirnaid, entrez);
                        }
                        else
                            firstLine = false;
                    } catch (Exception e) {}
                }
                br.close();
                in.close();
                fstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mirnaid2entrez;
    }
    
    public static String getEntrezFromSymbol(String symbol) {
        HashMap<String, String> data = getSymbol2Entrez();
        if (!data.isEmpty()) {
            for (String gs: data.keySet()) {
                if (gs.trim().toLowerCase().equals(symbol.trim().toLowerCase()))
                    return data.get(gs);
            }
        }
        return null;
    }
    
    public static String getEntrezFromMirnaID(String mirnaid) {
        HashMap<String, String> data = getMirnaID2Entrez();
        if (!data.isEmpty()) {
            for (String mid: data.keySet()) {
                if (mid.trim().toLowerCase().equals(mirnaid.trim().toLowerCase()))
                    return data.get(mid);
            }
        }
        return null;
    }
    
}
