package opengdc.additional;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import opengdc.util.GDCData;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class FinalTable {

    /**
     * @param args
     */
    public static void main(String[] args) {
        String server = "150.146.100.179";
        int port = 21;
        String user = "eleonora";
        String pass = "";
        String program = "tcga"; //args[0];
        FTPClient ftpClient = new FTPClient();
        try {
            FileWriter fileWriter = new FileWriter("/Users/eleonora/Desktop/final_table_"+program+".json") ;
            ftpClient.connect(server, port);
            ftpClient.login(user, pass);
            ftpClient.enterLocalPassiveMode();
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            //fileWriter.append("Tumor tag\tTumor name\tExperiment\t# Aliquots\t# Samples\t# Patients"+"\n");
            // APPROACH #1: using retrieveFile(String, OutputStream)
            fileWriter.append("{\n\"newData\": [\n");
            ArrayList<String> tumors = getTumors(program);
            int countTumor = 0;
            for(String tumor: tumors){
                try {
                    int totAliquots = 0;
                    int totSamples = 0;
                    int totPatients = 0;
                    String fullName = getFullName(ftpClient,"opengdc/bed/"+program+"/"+program+"-"+tumor+"/meta_dictionary_"+tumor+".txt");
                    ArrayList<String> experiemnts = getExperiments();
                    int countExp = 0;
                    for (String experiment: experiemnts) {
                        try{
                            countExp = countExp + 1;
                            ftpClient = disconnectAndReconnect(ftpClient,server,port,user,pass);
                            String remoteFile1 = "opengdc/bed/"+program+"/"+program+"-"+tumor+"/"+experiment+"/exp_info.tsv";
                            //  File downloadFile1 = new File("/Users/eleonora/Downloads/fileprova_exp_info.tsv");
                            InputStream in = ftpClient.retrieveFileStream(remoteFile1);
                            InputStreamReader ins = new InputStreamReader(in);
                            BufferedReader br = new BufferedReader(ins);
                            String line = null;
                            String aliquot = "";
                            String sample = "";
                            String patient = "";
                            while ((line = br.readLine()) != null ){
                                if (line.contains("aliquots")) {
                                    aliquot = line.split("\t")[1];
                                    totAliquots = totAliquots + Integer.valueOf(aliquot);
                                }
                                if (line.contains("samples")) {
                                    sample = line.split("\t")[1];
                                    totSamples = totSamples + Integer.valueOf(sample);
                                }
                                if (line.contains("patients")) {
                                    patient = line.split("\t")[1];
                                    totPatients = totPatients + Integer.valueOf(patient);
                                }
                            }
                            in.close();
                            ins.close();
                            br.close();
                            fileWriter.append("[\""+tumor+"\",\""+fullName+"\",\""+experiment.replaceAll("_", " ")+"\","+aliquot+","+sample+","+patient+"],\n");
                        } catch (Exception e) { }
                    }
                    countTumor = countTumor+1; 
                    ftpClient = disconnectAndReconnect(ftpClient,server,port,user,pass);
                    if (countExp==experiemnts.size() && countTumor==tumors.size())
                        fileWriter.append("[\""+tumor+"\",\""+fullName+"\",\"total\","+totAliquots+","+totSamples+","+totPatients+"]\n");	
                    else 
                        fileWriter.append("[\""+tumor+"\",\""+fullName+"\",\"total\","+totAliquots+","+totSamples+","+totPatients+"],\n");
                } catch (Exception e) { }
            }
            fileWriter.append("]\n}");
            fileWriter.flush();
            fileWriter.close();

        } catch (IOException ex) {
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            try {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private static FTPClient disconnectAndReconnect(FTPClient ftpClient, String server, int port, String user, String pass) throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
        FTPClient ftpClient1 = new FTPClient();
        ftpClient1.connect(server, port);
        ftpClient1.login(user, pass);
        ftpClient1.enterLocalPassiveMode();
        ftpClient1.setFileType(FTP.BINARY_FILE_TYPE);
        return ftpClient1 ;
    }

    public static ArrayList<String> getTumors(String program){
        ArrayList<String> map = new  ArrayList<> ();
        HashMap<String, HashMap<String, HashSet<String>>> dataMap = GDCData.getBigGDCDataMap();
        for (String tumor: dataMap.get( program.toUpperCase() ).keySet())
            map.add( tumor.toLowerCase().split("-")[1] );
        return map;
    }

    public static ArrayList<String> getExperiments() {
        ArrayList<String> experiments = new ArrayList<>();
        experiments.add("gene_expression_quantification");
        experiments.add("mirna_expression_quantification");
        experiments.add("copy_number_segment");
        experiments.add("isoform_expression_quantification");
        experiments.add("masked_copy_number_segment");
        experiments.add("masked_somatic_mutation");
        experiments.add("methylation_beta_value");
        return experiments;
    }
    
    public static String getFullName(FTPClient ftpClient, String path) throws IOException{
        InputStream in = ftpClient.retrieveFileStream(path);
        InputStreamReader ins = new InputStreamReader(in);
        BufferedReader br = new BufferedReader(ins);
        String fullName = "";
        boolean found = false;
        String line = null;
        while ((line = br.readLine()) != null ) {
            if (found) {
                fullName = line;
		break;
            }
            if (line.contains("gdc__project__disease_type"))
                found = true;
        }
        in.close();
        ins.close();
        br.close();
        return fullName;
    }


}
