/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;

/**
 *
 * @author fabio
 */
public class CreateHumanMethTables {
    
    private static final String HUMAN_METH_27 = "/Users/fabio/Downloads/017d6de8-c354-44fa-a696-cf9563f6b322_jhu-usc.edu_BRCA.HumanMethylation27.3.lvl-3.TCGA-AR-A0TS-01A-11D-A112-05.gdc_hg38.txt";
    private static final String HUMAN_METH_27_MAP = "/Users/fabio/Downloads/HumanMethylation27Map.txt";
    private static final String HUMAN_METH_450 = "/Users/fabio/Downloads/00fbb7bf-b569-4df9-993e-c81d65b11438_jhu-usc.edu_BRCA.HumanMethylation450.11.lvl-3.TCGA-E2-A1IF-01A-11D-A145-05.gdc_hg38.txt";
    private static final String HUMAN_METH_450_MAP = "/Users/fabio/Downloads/HumanMethylation450Map.txt";
    
    public static void main(String[] args) {
        HashMap<String, HashMap<String, String>> hm27_map = readFile(HUMAN_METH_27);
        printMap(hm27_map, HUMAN_METH_27_MAP);
        HashMap<String, HashMap<String, String>> hm450_map = readFile(HUMAN_METH_450);
        printMap(hm450_map, HUMAN_METH_450_MAP);
    }
    
    public static HashMap<String, HashMap<String, String>> readFile(String file_path) {
        HashMap<String, HashMap<String, String>> result = new HashMap<>();
        try {
            InputStream fstream = new FileInputStream(file_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            boolean skipHeader = true;
            while ((line = br.readLine()) != null) {
                if (!skipHeader) {
                    if (!line.trim().equals("")) {
                        String[] line_split = line.split("\t");
                        String probe = line_split[0];
                        HashMap<String, String> coord = new HashMap<>();
                        coord.put("chr", line_split[2]);
                        coord.put("start", line_split[3]);
                        coord.put("end", line_split[4]);
                        result.put(probe, coord);
                    }
                }
                skipHeader = false;
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    public static void printMap(HashMap<String, HashMap<String, String>> map, String out_file_path) {
        try {
            FileOutputStream fos = new FileOutputStream(out_file_path);
            PrintStream out = new PrintStream(fos);
            out.println("Composite Element REF\tChromosome\tStart\tEnd");
            for (String probe: map.keySet()) {
                HashMap<String, String> coord = map.get(probe);
                out.println(probe+"\t"+coord.get("chr")+"\t"+coord.get("start")+"\t"+coord.get("end"));
            }
            out.close();
            fos.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
