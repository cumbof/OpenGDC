/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TimerTask;
import opengdc.action.Action;
import opengdc.action.ConvertDataAction;
import opengdc.action.DownloadDataAction;
import opengdc.util.FSUtils;
import opengdc.util.GDCData;
import opengdc.util.GDCQuery;
import opengdc.util.UpdateGDCData;

/**
 *
 * @author fabio
 */
public class UpdateTask extends TimerTask {
    
    private static String ftp_root;
    
    public UpdateTask(String ftp_root_arg) {
        ftp_root = ftp_root_arg;
    }

    @Override
    public void run() {
        System.out.println("----------------------------------------");
        System.out.println("Timer task started at: " + new Date());
        updateTask();
        System.out.println("Timer task finished at: " + new Date());
        System.out.println("----------------------------------------");
    }

    private void updateTask() {
        HashMap<String, HashMap<String, HashSet<String>>> GDCDataMap = GDCData.getBigGDCDataMap();
        HashMap<String, String> dataType2DirName = GDCData.getGDCData2FTPFolderName();
        for (String program: GDCDataMap.keySet()) {
            for (String tumor: GDCDataMap.get(program).keySet()) {
                for (String dataType: GDCDataMap.get(program).get(tumor)) {
                    try {
                        String original_local_data_dir = ftp_root + "original" + "/" + program.toLowerCase() + "/" + dataType2DirName.get(tumor.toLowerCase()) + "/"; 
                        String converted_local_data_dir = ftp_root + "bed" + "/" + program.toLowerCase() + "/" + dataType2DirName.get(tumor.toLowerCase()) + "/"; 
                        String downloadTmpDirPath = Settings.getTmpDir() + "_download/";
                        String updatetable_original_path = original_local_data_dir + Settings.getUpdateTableName();
                        if (!(new File(updatetable_original_path)).exists())
                            (new File(updatetable_original_path)).createNewFile();
                        HashMap<String, HashMap<String, String>> updatetable_original = UpdateGDCData.loadUpdateTable_original(updatetable_original_path);
                        String updatetable_converted_path = converted_local_data_dir + Settings.getUpdateTableName();
                        if (!(new File(updatetable_converted_path)).exists())
                            (new File(updatetable_converted_path)).createNewFile();
                        HashMap<String, HashMap<String, String>> updatetable_converted = UpdateGDCData.loadUpdateTable_converted(updatetable_converted_path);

                        String query_file_path = GDCQuery.query(tumor, dataType, 0);
                        HashMap<String, HashMap<String, String>> dataMap = GDCQuery.extractInfo(query_file_path);
                        for (String uuid: dataMap.keySet()) {
                            if (!FSUtils.filePrefixExists(uuid, original_local_data_dir)) {
                                String aliquot_uuid = getAliquotUUID(uuid, dataType);

                                String current_md5 = dataMap.get(uuid).get("md5sum");
                                String current_updated_datetime = dataMap.get(uuid).get("updated_datetime");
                                String current_file_name = dataMap.get(uuid).get("file_name");
                                //String current_file_id = dataMap.get(uuid).get("file_id");
                                String current_file_size = dataMap.get(uuid).get("file_size");

                                if (FSUtils.filePrefixExists(aliquot_uuid, converted_local_data_dir)) {
                                    String converted_file_uuid = updatetable_converted.get(aliquot_uuid).get("file_id");
                                    FSUtils.deleteFileWithPrefix(aliquot_uuid, converted_local_data_dir);
                                    FSUtils.deleteFileWithPrefix(converted_file_uuid, original_local_data_dir);
                                    // create tmp download dir if it does not exist
                                    if (!(new File(downloadTmpDirPath)).exists())
                                        (new File(downloadTmpDirPath)).mkdir();
                                    // download new original file
                                    Date file_downloadDate = new Date();
                                    DownloadDataAction.downloadSingleData(uuid, dataMap, downloadTmpDirPath, true, true);
                                    // modify updatetable_original
                                    updatetable_original.remove(current_file_name);
                                    HashMap<String, String> updateInfo = new HashMap<>();
                                    updateInfo.put("md5sum", current_md5);
                                    updateInfo.put("updated_datetime", current_updated_datetime);
                                    updateInfo.put("file_name", current_file_name);
                                    //updateInfo.put("file_id", current_file_id);
                                    updateInfo.put("file_size", current_file_size);
                                    updateInfo.put("downloaded_datetime", file_downloadDate.toString());
                                    updatetable_original.put(current_file_name, updateInfo);
                                    (new File(updatetable_original_path)).delete();
                                    for (String fileName: updatetable_original.keySet()) {
                                        //String fileRow = fileName + "\t" + updatetable.get(fileName).get("file_id") + "\t" + updatetable.get(fileName).get("file_size") + "\t" + updatetable.get(fileName).get("md5sum") + "\t" + updatetable.get(fileName).get("updated_datetime") + "\t" + updatetable.get(fileName).get("downloaded_datetime") + "\n";
                                        String fileRow = fileName + "\t" + updatetable_original.get(fileName).get("file_size") + "\t" + updatetable_original.get(fileName).get("md5sum") + "\t" + updatetable_original.get(fileName).get("updated_datetime") + "\t" + updatetable_original.get(fileName).get("downloaded_datetime") + "\n";
                                        Files.write((new File(updatetable_original_path)).toPath(), (fileRow).getBytes("UTF-8"), StandardOpenOption.APPEND);
                                    }
                                    // load updatetable again
                                    updatetable_original = UpdateGDCData.loadUpdateTable_original(updatetable_original_path);
                                    // modify updatetable_converted
                                    updatetable_converted.remove(aliquot_uuid);
                                }
                                else {
                                    // create tmp download dir if it does not exist
                                    if (!(new File(downloadTmpDirPath)).exists())
                                        (new File(downloadTmpDirPath)).mkdir();
                                    // download original file
                                    Date file_downloadDate = new Date();
                                    DownloadDataAction.downloadSingleData(uuid, dataMap, downloadTmpDirPath, true, true);
                                    // modify updatetable
                                    HashMap<String, String> updateInfo = new HashMap<>();
                                    updateInfo.put("md5sum", current_md5);
                                    updateInfo.put("updated_datetime", current_updated_datetime);
                                    updateInfo.put("file_name", current_file_name);
                                    //updateInfo.put("file_id", current_file_id);
                                    updateInfo.put("file_size", current_file_size);
                                    updateInfo.put("downloaded_datetime", file_downloadDate.toString());
                                    updatetable_original.put(current_file_name, updateInfo);
                                    //String fileRow = current_file_name + "\t" + updatetable.get(current_file_name).get("file_id") + "\t" + updatetable.get(current_file_name).get("file_size") + "\t" + updatetable.get(current_file_name).get("md5sum") + "\t" + updatetable.get(current_file_name).get("updated_datetime") + "\t" + updatetable.get(current_file_name).get("downloaded_datetime") + "\n";
                                    String fileRow = current_file_name + "\t" + updatetable_original.get(current_file_name).get("file_size") + "\t" + updatetable_original.get(current_file_name).get("md5sum") + "\t" + updatetable_original.get(current_file_name).get("updated_datetime") + "\t" + updatetable_original.get(current_file_name).get("downloaded_datetime") + "\n";
                                    Files.write((new File(updatetable_original_path)).toPath(), (fileRow).getBytes("UTF-8"), StandardOpenOption.APPEND);
                                }
                            }
                        }
                        
                        // convert data
                        Action convertAction = new ConvertDataAction();
                        String[] convert_params = new String[6];
                        convert_params[0] = "convert";
                        convert_params[1] = program;
                        convert_params[2] = tumor;
                        convert_params[3] = dataType;
                        convert_params[4] = "bed";
                        convert_params[5] = "false";
                        convert_params[6] = "true";
                        convert_params[7] = updatetable_converted_path;
                        Settings.setInputGDCFolder(downloadTmpDirPath);
                        Settings.setOutputConvertedFolder(converted_local_data_dir);
                        convertAction.execute(convert_params);
                        
                        // remove _download tmp dir
                        FSUtils.deleteDir(new File(downloadTmpDirPath));
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private String getAliquotUUID(String file_uuid, String dataType) {
        HashSet<String> attributes = new HashSet<>();
        String aliquot_id_path = "cases.samples.portions.analytes.aliquots.aliquot_id";
        attributes.add(aliquot_id_path);
        HashSet<String> dataTypes = new HashSet<>();
        dataTypes.add(dataType);
        HashMap<String, ArrayList<Object>> file_info = GDCQuery.retrieveExpInfoFromAttribute("files", "files.file_id", file_uuid, dataTypes, attributes, 0, 0, null).get(0);
        String aliquot_uuid = "";
        if (file_info != null) {
            if (file_info.containsKey("cases.samples.portions.analytes.aliquots.aliquot_id")) {
                for (String k: file_info.keySet()) {
                    for (Object obj: file_info.get(k)) {
                        HashMap<String, Object> map = (HashMap<String, Object>)obj;
                        for (String kmap: map.keySet()) {
                            try {
                                if (kmap.toLowerCase().equals("cases.samples.portions.analytes.aliquots.aliquot_id"))
                                    aliquot_uuid = String.valueOf(map.get(kmap));
                            }
                            catch (Exception e) { }
                        }
                    }
                }
            }
        }
        return aliquot_uuid;
    }
    
}
