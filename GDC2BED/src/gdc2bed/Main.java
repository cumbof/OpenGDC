/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gdc2bed;

import gdc2bed.util.GDCData;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        
        HashMap<String, HashMap<String, HashSet<String>>> gdcDataMap = GDCData.getBigGDCDataMap();
        for (String program: gdcDataMap.keySet()) {
            if (program.toLowerCase().trim().equals("tcga")) {
                for (String disease: gdcDataMap.get(program).keySet()) {
                    for (String dataType: gdcDataMap.get(program).get(disease)) {
                        if (dataType.toLowerCase().trim().equals("masked somatic mutation")) {
                            System.err.println(program + "\t" + disease + "\t" + dataType);
                            
                            /** DOWNLOAD DATA **/
                            /*String outDirStr = "/Users/fabio/Downloads/test_gdc_download/"+program+"/"+disease+"/";
                            File outDir = new File(outDirStr);
                            outDir.mkdirs();
                            Settings.setOutputGDCFolder(outDirStr);
                            
                            String[] arr = new String[6];
                            arr[0] = "download";            // Action name
                            arr[1] = program;               // Program
                            arr[2] = disease;               // Disease
                            arr[3] = dataType;              // Data type
                            arr[4] = "true";                // Auto-extract data
                            arr[5] = "true";                // Auto-remove data

                            Controller controller = new Controller();
                            controller.execute(arr);*/
        
                            // -------------------------------------------------
                            
                            /** CONVERT DATA **/
                            String inDirStr = "/Users/fabio/Downloads/test_gdc_download/"+program+"/"+disease+"/";
                            String outDirStr = "/Users/fabio/Downloads/test_gdc_download/BED/"+disease+"/";
                            File outDir = new File(outDirStr);
                            outDir.mkdirs();
                            Settings.setInputGDCFolder(inDirStr);
                            Settings.setOutputConvertedFolder(outDirStr);
                            
                            String[] arr = new String[5];
                            arr[0] = "convert";             // Action name
                            arr[1] = program;               // Program
                            arr[2] = disease;               // Disease
                            arr[3] = dataType;              // Data type
                            arr[4] = "BED";                 // Format

                            Controller controller = new Controller();
                            controller.execute(arr);
                        }
                    }
                }
            }
        }
        
        
        
    }
    
}
