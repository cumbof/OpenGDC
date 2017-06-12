/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
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
    private static final String CMD = "download and convert";
    
    private static void initSkipDiseases() {
        skip_diseases = new HashSet<>();
        //skip_diseases.add("");
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        initSkipDiseases();
        
        HashMap<String, HashMap<String, HashSet<String>>> gdcDataMap = GDCData.getBigGDCDataMap();
        for (String program: gdcDataMap.keySet()) {
            if (program.toLowerCase().trim().equals("target")) {
                for (String disease: gdcDataMap.get(program).keySet()) {
                    if (!skip_diseases.contains(disease)) {
                        HashSet<String> dataTypes = new HashSet<>();
                        dataTypes.add("Clinical and Biospecimen Supplements");
                        //for (String dataType: gdcDataMap.get(program).get(disease)) {
                        for (String dataType: dataTypes) {    
                            if (dataType.toLowerCase().trim().contains("clinical") || dataType.toLowerCase().trim().contains("biospecimen")) {
                                System.err.println(program + "\t" + disease + "\t" + dataType);

                                if (CMD.trim().toLowerCase().contains("download")) {
                                    /** DOWNLOAD DATA **/
                                    String outDirStr = "/Users/fabio/Downloads/test_gdc_download/TARGET-Metadata/"+program+"/"+disease+"/gdc/";
                                    //String outDirStr = "D:/htdocs/gdcwebapp/assets/metadata/"+disease+"/gdc/";

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
                                    controller.execute(arr);
                                }
                                if (CMD.trim().toLowerCase().contains("convert")) {
                                    /** CONVERT DATA **/
                                    String inDirStr = "/Users/fabio/Downloads/test_gdc_download/TARGET-Metadata/"+program+"/"+disease+"/gdc/";
                                    //String inDirStr = "D:/htdocs/gdcwebapp/assets/metadata/"+disease+"/gdc/";
                                    String outDirStr = "/Users/fabio/Downloads/test_gdc_download/TARGET-Metadata/"+program+"/"+disease+"/meta/";
                                    //String outDirStr = "D:/htdocs/gdcwebapp/assets/metadata/"+disease+"/meta/";

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
    
}
