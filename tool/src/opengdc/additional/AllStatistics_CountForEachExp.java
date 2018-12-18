/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.additional;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import opengdc.util.GDCData;

/**
 *
 * @author fabio
 */
public class AllStatistics_CountForEachExp {
    
    private static final String BASE_PATH = "/FTP/ftp-root/opengdc/bed/tcga/";
    private static final String PROGRAM = "tcga";
    
    //private static HashMap<String, HashMap<String, Integer>> countMap = new HashMap<>();
    private static HashMap<String, HashMap<String, HashSet<String>>> countMap = new HashMap<>();
    
    public static void main(String[] args) {
        
        HashMap<String, String> dataTypes = GDCData.getGDCData2FTPFolderName();
        HashMap<String, HashMap<String, HashSet<String>>> gdcMap = GDCData.getBigGDCDataMap();
        
        for (String dataType: dataTypes.keySet()) {
            if (!dataType.toLowerCase().equals("clinical and biospecimen supplements")) {
                HashSet<String> aliquot_ids = new HashSet<>();
                HashSet<String> sample_ids = new HashSet<>();
                HashSet<String> case_ids = new HashSet<>();

                for (String tumor: gdcMap.get(PROGRAM.toUpperCase()).keySet()) {
                    try {
                        String tumor_tag = tumor.toLowerCase();
                        String metadict_path = BASE_PATH + tumor_tag + "/" + dataTypes.get(dataType).toLowerCase() + "/meta_dictionary.txt";

                        aliquot_ids.addAll( readDict(metadict_path, "gdc__aliquots__aliquot_id") );
                        sample_ids.addAll( readDict(metadict_path, "gdc__samples__sample_id") );
                        case_ids.addAll( readDict(metadict_path, "gdc__case_id") );
                    }
                    catch (Exception e) {
                        System.err.println(dataType + " does not exist for the tumor " + tumor);
                    }
                }

                //HashMap<String, Integer> attr2count = new HashMap<>();
                //attr2count.put("aliquot", aliquot_ids.size());
                //attr2count.put("sample", sample_ids.size());
                //attr2count.put("patient", case_ids.size());
                HashMap<String, HashSet<String>> attr2count = new HashMap<>();
                attr2count.put("aliquot", aliquot_ids);
                attr2count.put("sample", sample_ids);
                attr2count.put("patient", case_ids);

                countMap.put(dataType, attr2count);
            }
        }
        
        HashSet<String> total_aliquots = new HashSet<>();
        HashSet<String> total_samples = new HashSet<>();
        HashSet<String> total_patients = new HashSet<>();
        
        for (String dataType: countMap.keySet()) {
            System.err.println(dataType);
            for (String attr: countMap.get(dataType).keySet()) {
                System.err.println("\t" + attr + " : " + countMap.get(dataType).get(attr).size());
                if (attr.equals("aliquot"))
                    total_aliquots.addAll(countMap.get(dataType).get(attr));
                else if (attr.equals("sample"))
                    total_samples.addAll(countMap.get(dataType).get(attr));
                else if (attr.equals("patient"))
                    total_patients.addAll(countMap.get(dataType).get(attr));
            }
        }
        
        System.err.println("total");
        System.err.println("\taliquot : " + total_aliquots.size());
        System.err.println("\tsample : " + total_samples.size());
        System.err.println("\tpatient : " + total_patients.size());
    }
    
    public static HashSet<String> readDict(String metadict_path, String attribute) {
        HashSet<String> values = new HashSet<>();
        try {
            InputStream fstream = new FileInputStream(metadict_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null) {
                if (!found) {
                    if (line.trim().toLowerCase().equals(attribute))
                        found = true;
                }
                else {
                    if (!line.trim().equals(""))
                        values.add(line.trim());
                    else break;
                }
            }
            br.close();
            in.close();
            fstream.close();
            return values;
        } catch (Exception e) {
            //e.printStackTrace();
            return null;
        }
    }
    
}
