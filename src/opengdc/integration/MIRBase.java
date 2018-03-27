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
    
    private static String mirbase_table_path = Settings.getMirbaseHsaDataPath();
    
    /*
    * mirnaid2coordinates.values : {CHR, START, END, STRAND}
    */
    private static HashMap<String, HashMap<String, String>> mirnaid2coordinates = new HashMap<>();
    
    public static HashMap<String, HashMap<String, String>> getMirnaid2coordinates() {
        if (mirnaid2coordinates.isEmpty()) {
            try {
                InputStream fstream = new FileInputStream(mirbase_table_path);
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
                            String original_symbol = "";
                            for (String var: id_split) {
                                if (var.toLowerCase().startsWith("name")) {
                                    original_symbol = var.split("=")[1];
                                    break;
                                }
                            }
                            if (!original_symbol.equals("")) {
                                info.put("MIRBASE_SYMBOL", original_symbol);
	                        mirnaid2coordinates.put(original_symbol, info);
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
        }
        return mirnaid2coordinates;
    }
    
}
