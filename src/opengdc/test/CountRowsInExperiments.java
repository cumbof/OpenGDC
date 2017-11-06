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

/**
 *
 * @author fabio
 */
public class CountRowsInExperiments {
    
    public static void main(String[] args) {
        String root_path = args[0];
        String program_disease = args[1];
        String disease = program_disease.split("-")[1];
        int current_size = 0;
        for (File f: (new File(root_path)).listFiles()) {
            if (f.getName().toUpperCase().contains("_"+disease.toUpperCase()+".") && f.getName().endsWith("txt")) {
                int rows = getRowCount(f);
                if (current_size == 0)
                    current_size = rows;
                if (rows != current_size) {
                    System.err.println("current_size: "+current_size+" - rows: "+rows+" - file: "+f.getName());
                    return;
                }
            }
        }
        System.err.println("current_size: "+current_size);
    }

    private static int getRowCount(File f) {
        int row_count = 0;
        try {
            InputStream fstream = new FileInputStream(f.getAbsolutePath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            boolean skipHeader = true;
            while ((line = br.readLine()) != null) {
                if (!skipHeader) {
                    if (!line.trim().equals("")) {
                        row_count++;
                    }
                }
                skipHeader = false;
            }
            br.close();
            in.close();
            fstream.close();
        }
        catch (Exception e) {}
        return row_count;
    }
    
}
