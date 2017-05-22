/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.reader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

/**
 *
 * @author fabio
 */
public class GeneExpressionQuantificationReader {
    
    public static HashMap<String, String> getEnsembl2Value(File expFile) {
        HashMap<String, String> ensembl2value = new HashMap<>();
        
        try {
            InputStream fstream = new FileInputStream(expFile.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] line_split = line.split("\t");
                    ensembl2value.put(line_split[0], line_split[1]);
                }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return ensembl2value;
    }
    
}
