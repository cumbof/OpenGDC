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
public class MIRBase {
    
    /*
    * mirnaid2coordinates.values : {CHR, START, END, STRAND}
    */
    private static HashMap<String, HashMap<String, String>> mirnaid2coordinates = new HashMap<>();
    
    public static HashMap<String, HashMap<String, String>> getMirnaid2coordinates() {
        if (mirnaid2coordinates.isEmpty()) {
            try {
                InputStream fstream = new FileInputStream(Settings.getMirbaseHsaPath());
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    try {
                        if (!line.startsWith("#")) {
                            String[] arr = line.split("\t");
                            HashMap<String, String> info = new HashMap<>();
                            info.put("STRAND", arr[6]);
                            info.put("START", arr[3]);
                            info.put("END", arr[4]);
                            info.put("CHR", arr[0]);

                            String[] id_split = arr[arr.length-1].split(";");
                            String original_symbol = "null";
                            for (String var: id_split) {
                                if (var.toLowerCase().startsWith("name"))
                                    original_symbol = var.split("=")[1];
                                break;
                            }
                            info.put("MIRBASE_SYMBOL", original_symbol);

                            mirnaid2coordinates.put(original_symbol, info);
                        }
                    } catch (Exception e) {}
                }
                br.close();
                in.close();
                fstream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mirnaid2coordinates;
    }
    
}
