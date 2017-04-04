/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc;

import opengdc.util.GDCData;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class Main {
    
    private static HashSet<String> skip_diseases = new HashSet<>();
    
    private static void initSkipDiseases() {
        skip_diseases = new HashSet<>();
        skip_diseases.add("TCGA-CHOL");
        skip_diseases.add("TCGA-COAD");
        skip_diseases.add("TCGA-DLBC");
        skip_diseases.add("TCGA-ESCA");
        skip_diseases.add("TCGA-KICH");
        skip_diseases.add("TCGA-KIRC");
        skip_diseases.add("TCGA-OV");
        skip_diseases.add("TCGA-PAAD");
        skip_diseases.add("TCGA-READ");
        skip_diseases.add("TCGA-SARC");
        skip_diseases.add("TCGA-SKCM");
        skip_diseases.add("TCGA-STAD");
        skip_diseases.add("TCGA-THYM");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        initSkipDiseases();
        
        HashMap<String, HashMap<String, HashSet<String>>> gdcDataMap = GDCData.getBigGDCDataMap();
        for (String program: gdcDataMap.keySet()) {
            if (program.toLowerCase().trim().equals("tcga")) {
                for (String disease: gdcDataMap.get(program).keySet()) {
                    if (!skip_diseases.contains(disease)) {
                        for (String dataType: gdcDataMap.get(program).get(disease)) {
                            if (dataType.toLowerCase().trim().equals("clinical supplement") || dataType.toLowerCase().trim().equals("biospecimen supplement")) {
                                System.err.println(program + "\t" + disease + "\t" + dataType);

                                /** DOWNLOAD DATA **/
                                /*String outDirStr = "/Users/fabio/Downloads/test_gdc_download/"+program+"/"+disease+"/";
                                //String outDirStr = "";
                                //if (dataType.toLowerCase().trim().equals("clinical supplement"))
                                    //outDirStr = "/Users/fabio/Downloads/test_gdc_download/TCGA-Metadata/"+disease+"/clinical/";
                                //else if (dataType.toLowerCase().trim().equals("biospecimen supplement"))
                                    //outDirStr = "/Users/fabio/Downloads/test_gdc_download/TCGA-Metadata/"+disease+"/biospecimen/";

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
                                //String inDirStr = "/Users/fabio/Downloads/test_gdc_download/"+program+"/"+disease+"/";
                                String inDirStr = "";
                                if (dataType.toLowerCase().trim().equals("clinical supplement"))
                                    inDirStr = "/Users/fabio/Downloads/test_gdc_download/TCGA-Metadata/"+disease+"/clinical/";
                                else if (dataType.toLowerCase().trim().equals("biospecimen supplement"))
                                    inDirStr = "/Users/fabio/Downloads/test_gdc_download/TCGA-Metadata/"+disease+"/biospecimen/";
                                //String outDirStr = "/Users/fabio/Downloads/test_gdc_download/BED/"+disease+"/";
                                String outDirStr = "";
                                if (dataType.toLowerCase().trim().equals("clinical supplement"))
                                    outDirStr = "/Users/fabio/Downloads/test_gdc_download/BED-Metadata/"+disease+"/clinical/";
                                else if (dataType.toLowerCase().trim().equals("biospecimen supplement"))
                                    outDirStr = "/Users/fabio/Downloads/test_gdc_download/BED-Metadata/"+disease+"/biospecimen/";
                                    
                                File outDir = new File(outDirStr);
                                outDir.mkdirs();
                                Settings.setInputGDCFolder(inDirStr);
                                Settings.setOutputConvertedFolder(outDirStr);

                                String[] arr = new String[5];
                                arr[0] = "convert";             // Action name
                                arr[1] = program;               // Program
                                arr[2] = disease;               // Disease
                                arr[3] = dataType;              // Data type
                                //arr[4] = "BED";                 // Format
                                arr[4] = "META";
                                
                                Controller controller = new Controller();
                                controller.execute(arr);
                            }
                        }
                    }
                }
            }
        }
                
    }
    
}
