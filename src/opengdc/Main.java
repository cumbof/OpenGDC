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
    private static HashSet<String> todo_diseases = new HashSet<>();
    private static HashSet<String> skip_datatypes = new HashSet<>();
    //private static final String CMD = "download and convert";
    private static final String CMD = "download and convert";
    private static final String ROOT = "/FTP/ftp-root/";
    //private static final String ROOT = "/Users/fabio/Downloads/test_gdc_download/";
    
    private static void initDiseases() {
        todo_diseases = new HashSet<>();
        todo_diseases.add("tcga-uvm");
        todo_diseases.add("tcga-ucs");
        todo_diseases.add("tcga-ucec");
        todo_diseases.add("tcga-tgct");
        todo_diseases.add("tcga-pcpg");
        todo_diseases.add("tcga-meso");
        todo_diseases.add("tcga-luad");
        todo_diseases.add("tcga-lihc");
        todo_diseases.add("tcga-kirp");
        todo_diseases.add("tcga-gbm");
        todo_diseases.add("tcga-cesc");
        todo_diseases.add("tcga-blca");
    }
    
    private static void initSkipDiseases() {
        skip_diseases = new HashSet<>();
        skip_diseases.add("tcga-thca");
        skip_diseases.add("tcga-kirc");
        skip_diseases.add("tcga-ov");
        skip_diseases.add("tcga-brca");
        skip_diseases.add("tcga-acc");
        skip_diseases.add("tcga-stad");
        skip_diseases.add("tcga-kich");
        skip_diseases.add("tcga-chol");
        skip_diseases.add("tcga-thym");
        skip_diseases.add("tcga-sarc");
        skip_diseases.add("tcga-skcm");
        skip_diseases.add("tcga-dlbc");
        skip_diseases.add("tcga-paad");
        skip_diseases.add("tcga-coad");
        skip_diseases.add("tcga-esca");
        skip_diseases.add("tcga-read");
        skip_diseases.add("tcga-lusc");
    }
    
    private static void initSkipDataTypes() {
        skip_datatypes = new HashSet<>();
        /*skip_datatypes.add("clinical supplement");
        skip_datatypes.add("biospecimen supplement");
        skip_datatypes.add("gene expression quantification");
        skip_datatypes.add("methylation beta value");
        skip_datatypes.add("copy number segment");
        skip_datatypes.add("isoform expression quantification");*/
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        /*String inputProgram = null;
        String inputDiseaseAbbreviation = null;
        if (args.length == 2) {
            inputProgram = args[0];
            inputDiseaseAbbreviation = args[1];
        }
        else {
            System.err.println("Usage example: java -jar app.jar 'program' 'disease'");
            System.exit(1);
        }*/
        
        //String inputProgram = "TCGA";
        //String inputDiseaseAbbreviation = "ACC";
        
        //String inputDisease = (inputProgram+"-"+inputDiseaseAbbreviation).toLowerCase();
        
        initSkipDiseases();
        initSkipDataTypes();
        initDiseases();
        
        HashMap<String, HashMap<String, HashSet<String>>> gdcDataMap = GDCData.getBigGDCDataMap();
        for (String program: gdcDataMap.keySet()) {
            if (program.toLowerCase().trim().equals("tcga")) {
                for (String disease: gdcDataMap.get(program).keySet()) {
                    //if (disease.toLowerCase().equals(inputDisease.toLowerCase())) {
                        if (!skip_diseases.contains(disease.toLowerCase())) {
                        //if (todo_diseases.contains(disease.toLowerCase())) {
                            //HashSet<String> dataTypes = new HashSet<>();
                            //dataTypes.add("Clinical and Biospecimen Supplements");
                            for (String dataType: gdcDataMap.get(program).get(disease)) {
                            //for (String dataType: dataTypes) {
                                if (dataType.toLowerCase().contains("clinical"))
                                    dataType = "Clinical and Biospecimen Supplements";
                                else if (dataType.toLowerCase().contains("biospecimen"))
                                    dataType = null;
                                if (dataType != null) {
                                    if (!skip_datatypes.contains(dataType.toLowerCase())) {
                                        System.err.println(program + "\t" + disease + "\t" + dataType);
                                        try {
                                            if (CMD.trim().toLowerCase().contains("download")) {
                                                /** DOWNLOAD DATA **/
                                                //String outDirStr = "/Users/fabio/Downloads/test_gdc_download/"+program+"/"+disease+"/gdc/";
                                                String outDirStr = ROOT+"opengdc/original/"+program.toLowerCase()+"/"+disease.toLowerCase()+"/"+GDCData.getGDCData2FTPFolderName().get(dataType.toLowerCase())+"/";
                                                //String outDirStr = ROOT+disease.toLowerCase().split("-")[1]+"/original/";
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
                                                String inDirStr = ROOT+"opengdc/original/"+program.toLowerCase()+"/"+disease.toLowerCase()+"/"+GDCData.getGDCData2FTPFolderName().get(dataType.toLowerCase())+"/";
                                                //String inDirStr = ROOT+disease.toLowerCase().split("-")[1]+"/original/";
                                                //String inDirStr = "/Users/fabio/Downloads/test_gdc_download/"+program+"/"+disease.split("-")[1]+"/gdc/";
                                                //String inDirStr = "D:/htdocs/gdcwebapp/assets/metadata/"+disease+"/gdc/";
                                                //String outDirStr = "/DATA/ftp-root/opengdc/bed/"+program.toLowerCase()+"/"+disease.toLowerCase()+"/clinical_and_biospecimen_supplements/";
                                                String outDirStr = ROOT+"opengdc/bed/"+program.toLowerCase()+"/"+disease.toLowerCase()+"/"+GDCData.getGDCData2FTPFolderName().get(dataType.toLowerCase())+"/";
                                                //String outDirStr = "/Users/fabio/Downloads/test_gdc_download/"+program+"/"+disease.split("-")[1]+"/meta/";
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
                                                // Format
                                                if (dataType.toLowerCase().contains("clinical"))
                                                    arr[4] = "META";
                                                else
                                                    arr[4] = "BED";

                                                Controller controller = new Controller();
                                                controller.execute(arr);
                                            }
                                        }
                                        catch (Exception e) {
                                            System.err.println("An error has occurred for: "+disease);
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                    //}
                }
            }
        }
                
    }
    
}
