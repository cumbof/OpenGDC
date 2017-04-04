/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.action;

import opengdc.GUI;
import opengdc.Settings;
import opengdc.util.DataExtractionTool;
import opengdc.util.FSUtils;
import opengdc.util.GDCQuery;
import java.io.File;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class DownloadDataAction extends Action {

    @Override
    public void execute(String[] args) {
        // skip the first entry -> download
        //String action = args[0];
        //String program = args[1];
        String disease = args[2];
        String dataType = args[3];
        boolean autoextract = Boolean.valueOf(args[4]);
        boolean autoremove = Boolean.valueOf(args[5]);
        String gdc_path = Settings.getOutputGDCFolder();
        
        System.err.println("Downloading GDC Data" + "\n" + "Disease: " + disease + "\n" + "Data Type: " + dataType + "\n" + "Output Folder Path: " + gdc_path + "\n" + "Auto-extract: " + autoextract + "\n" + "Auto-remove: " + autoremove + "\n");
        GUI.appendLog("Downloading GDC Data" + "\n" + "Disease: " + disease + "\n" + "Data Type: " + dataType + "\n" + "Output Folder Path: " + gdc_path + "\n" + "Auto-extract: " + autoextract + "\n" + "Auto-remove: " + autoremove + "\n");
        
        GDCQuery.query(disease, dataType);
        HashMap<String, HashMap<String, String>> dataMap = GDCQuery.extractInfo(GDCQuery.getLastQueryFilePath());
        GUI.appendLog("Data Amount: " + dataMap.size() + " files" + "\n\n");
        
        // TODO activate progress bar
        // TODO show stack in Log tab
        
        /* DOWNLOAD ALL FILE */
        //GDCQuery.downloadFiles((new HashSet<>(dataMap.keySet())), gdc_path);
        
        /* DOWNLOAD FILE BY FILE */
        /*for (String uuid: dataMap.keySet())
            GDCQuery.downloadFile(uuid, gdc_path, dataMap.get(uuid).get("file_name"));*/
        
        /* AUTOEXTRACT PACKAGES */
        /*if (autoextract) {
            for (String uuid: dataMap.keySet()) {
                File data_file = new File(gdc_path + dataMap.get(uuid).get("file_name"));
                if (data_file.exists()) {
                    if (data_file.getName().toLowerCase().endsWith(".tar.gz")) {
                        DataExtractionTool.uncompressTarGz(data_file, new File(gdc_path), false);
                    }
                    else if (data_file.getName().toLowerCase().endsWith(".gz")) {
                        String gzFileName = dataMap.get(uuid).get("file_name");
                        String fileName = gzFileName.substring(0, gzFileName.length()-3);
                        DataExtractionTool.uncompressGz(data_file, new File(gdc_path + fileName), false);
                    }
                }
            }
        }*/
        
        
        /* DOWNLOAD (AND EXTRACT (AND REMOVE)) FILE BY FILE */
        for (String uuid: dataMap.keySet()) {
            // download data
            GDCQuery.downloadFile(uuid, gdc_path, dataMap.get(uuid).get("file_name"));
            if (autoextract) {
                HashSet<String> uncompressed_folders_path = new HashSet<>();
                HashSet<String> experiments_path = new HashSet<>();
                File data_file = new File(gdc_path + dataMap.get(uuid).get("file_name"));
                if (data_file.exists()) {
                    String destDirPath = gdc_path + "/" + data_file.getName() + "_/";
                    File destDir = new File(destDirPath);
                    
                    // extract data
                    boolean uncompressed = DataExtractionTool.uncompressData(data_file, destDir, false);
                    //System.out.println("uncompressed: " + uncompressed);
                    uncompressed_folders_path.addAll(DataExtractionTool.getUncompressedFoldersPathList());
                    experiments_path.addAll(DataExtractionTool.getExperimentsPathList());
                    
                    // copy experiments to the gdc_path folder
                    for (String exp: experiments_path) {
                        try {
                            File expFile = new File(exp);
                            File newExpFile = new File(gdc_path + expFile.getName());
                            Files.move(expFile.toPath(), newExpFile.toPath(), REPLACE_EXISTING);
                        }
                        catch (Exception e) {
                            //e.printStackTrace();
                        }
                    }
                    
                    // remove other files and folders
                    for (String dir: uncompressed_folders_path) {
                        File dirFolder = new File(dir);
                        FSUtils.deleteDir(dirFolder);
                    }
                    
                    // remove original data
                    if (uncompressed && autoremove) {
                        data_file.delete();
                    }
                }
            }
        }
        
        System.err.println("\n" + "done" + "\n\n" + "#####################" + "\n\n");
        GUI.appendLog("\n" + "done" + "\n\n" + "#####################" + "\n\n");
    }
    
}
