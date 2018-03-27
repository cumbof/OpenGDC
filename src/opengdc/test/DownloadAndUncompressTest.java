package opengdc.test;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.HashMap;
import java.util.HashSet;
import opengdc.util.DataExtractionTool;
import opengdc.util.GDCQuery;

/**
 *
 * @author fabio
 */
public class DownloadAndUncompressTest {
    
    public static final String disease = "TCGA-STAD";
    public static final String dataType = "Methylation Beta Value";
    public static final int expectedFieldsPerRow = 11; // 11 columns for DNA Methylation exp
    public static final String gdc_path = "/Users/fabio/Downloads/test_gdc_download/test/";
    public static final boolean autoextract = true;
    public static final boolean autoremove = false;
    
    public static void main(String[] args) {
        String query_file_path = GDCQuery.query(disease, dataType, 0);
        HashMap<String, HashMap<String, String>> dataMap = GDCQuery.extractInfo(query_file_path);
        
        /* DOWNLOAD (AND EXTRACT (AND REMOVE)) FILE BY FILE */
        for (String uuid: dataMap.keySet()) {
            // download data
            String fileName = uuid + "_" + dataMap.get(uuid).get("file_name");
            for (String s: dataMap.get(uuid).keySet())
                System.err.println(s + "\t" + dataMap.get(uuid).get(s));
            
            GDCQuery.downloadFile(uuid, gdc_path, fileName, false, 0, null);
            if (autoextract) {
                HashSet<String> experiments_path = new HashSet<>();
                File data_file = new File(gdc_path + fileName);
                if (data_file.exists()) {
                    String destDirPath = gdc_path + "/" + data_file.getName() + "_/";
                    File destDir = new File(destDirPath);
                    
                    // extract data
                    boolean uncompressed = DataExtractionTool.uncompressData(data_file, destDir, false, true);
                    //System.out.println("uncompressed: " + uncompressed);
                    experiments_path.addAll(DataExtractionTool.getExperimentsPathList());
                    
                    // copy experiments to the gdc_path folder
                    for (String exp: experiments_path) {
                        try {
                            File expFile = new File(exp);
                            File newExpFile = new File(gdc_path + expFile.getName());
                            Files.move(expFile.toPath(), newExpFile.toPath(), REPLACE_EXISTING);
                            
                            if (isCorrupted(newExpFile.getAbsolutePath(), expectedFieldsPerRow))
                                System.err.println("-------> CORRUPTED: "+newExpFile.getAbsolutePath());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    // remove original data
                    if (uncompressed && autoremove) {
                        data_file.delete();
                    }
                }
            }
        }
    }
    
    public static boolean isCorrupted(String data_file_path, int expected_fields) {
        boolean is_corrupted = false;
        try {
            InputStream fstream = new FileInputStream(data_file_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] line_split = line.split("\t");
                    if (line_split.length != expected_fields) {
                        is_corrupted = true;
                        break;
                    }
                }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            is_corrupted = true;
            e.printStackTrace();
        }
        return is_corrupted;
    }
    
}
