/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
 */
package opengdc.integration;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import opengdc.Settings;
import opengdc.util.DownloadUtils;

/**
 *
 * @author fabio
 */
public class NCBI {
    
    private static final String GENOME_VERSION = "grch38";
    private static HashMap<String, HashMap<String, String>> ncbi_data = new HashMap<>();
    private static String ncbi_table_path = Settings.getNCBIDataPath();
    
    public static boolean updateNCBIData(String gdc_entrez, String gdc_gene, String chr, String start, String end, String strand) {
        File ncbidata = new File(ncbi_table_path);
        if (ncbidata.exists()) {
            try {
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(ncbidata.getAbsolutePath(), true), "UTF-8"));
                output.append(gdc_entrez + "\t" + gdc_gene + "\t" + chr + "\t" + start + "\t" + end + "\t" + strand);
                output.newLine();
                output.close();
                
                //loadNCBIData(true);
                HashMap<String, String> info = new HashMap<>();
                info.put("STRAND", strand);
                info.put("START", start);
                info.put("END", end);
                info.put("CHR", chr);
                info.put("GDC_SYMBOL", gdc_gene);
                info.put("GDC_ENTREZ", gdc_entrez);
                ncbi_data.put(gdc_entrez, info);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    public static HashMap<String, HashMap<String, String>> loadNCBIData(boolean forceLoad) {
        if (ncbi_data.isEmpty() || forceLoad) {
            ncbi_data = new HashMap<>();
            try {
                InputStream fstream = new FileInputStream(ncbi_table_path);
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        String[] arr = line.split("\t");
                        HashMap<String, String> info = new HashMap<>();
                        info.put("STRAND", arr[5]);
                        info.put("START", arr[3]);
                        info.put("END", arr[4]);
                        info.put("CHR", arr[2]);
                        info.put("GDC_SYMBOL", arr[1]);
                        info.put("GDC_ENTREZ", arr[0]);
                        String gdc_entrez = arr[0];
                        ncbi_data.put(gdc_entrez, info);
                    }
                }
                br.close();
                in.close();
                fstream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return ncbi_data;
    }
    
    public static HashMap<String, String> getGeneInfo(String entrez, String symbol) {
        HashMap<String, HashMap<String, String>> data = loadNCBIData(false);
        if (data.containsKey(entrez))
            return data.get(entrez);
        else {
            HashMap<String, String> gene_info = retrieveGenomicCoordinates(entrez, symbol);
            if (!gene_info.isEmpty()) {
	        updateNCBIData(gene_info.get("GDC_ENTREZ"), gene_info.get("GDC_SYMBOL"), gene_info.get("CHR"), gene_info.get("START"), gene_info.get("END"), gene_info.get("STRAND"));
	        return gene_info;
            }
        }
        return new HashMap<>();
    }

    public static HashMap<String, String> retrieveGenomicCoordinates(String entrez, String gene) {
        try {
            HashMap<String, String> result = new HashMap<>();
            HashMap<String, String> result_tmp = new HashMap<>();
            
            String strand = "";
            String chr = "";
            int start = -1;
            int end = -1;

            //String ncbiBiotabQuery = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi/?db=Gene&id=" + entrez + "&format=biotab";
            String ncbiBiotabQuery = "https://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi/?db=Gene&id=" + entrez;
            File ncbiBiotab_tmp = new File(Settings.getTmpDir()+"ncbi.efetch.fcgi.txt");
            DownloadUtils.downloadDataFromUrl(ncbiBiotabQuery, ncbiBiotab_tmp.getAbsolutePath(), 0);
            BufferedReader reader = new BufferedReader(new FileReader(ncbiBiotab_tmp.getAbsolutePath()));
            String line = reader.readLine();
            
            boolean genomeMatch = false; // match on GRCh38 genome version
            
            boolean replaced = false;
            boolean db_GeneID = false;
            
            while (line != null) {
                /////////////////////////////////////////////////
                if (line.trim().toLowerCase().contains("heading") && line.toLowerCase().contains(GENOME_VERSION))
                    genomeMatch = true;
                /////////////////////////////////////////////////
                if (line.trim().toLowerCase().contains("current-id")) {
                    replaced = true;
                }
                if (replaced) {
                    if (line.trim().toLowerCase().contains("db \"geneid\"")) {
                        db_GeneID = true;
                    }
                    if (db_GeneID) {
                        if (line.trim().toLowerCase().contains("tag id")) {
                            String[] line_split = line.split(" ");
                            String newEntrez = line_split[line_split.length-1];
                            result_tmp = retrieveGenomicCoordinates(newEntrez, gene);
                            result_tmp.put("GDC_ENTREZ", newEntrez);
                            break;
                        }
                    }
                }
                ////////////////////////////////////////////////
                    
                if (genomeMatch) {
                    if (line.trim().toLowerCase().contains("label") && line.trim().toLowerCase().contains("chromosome")) {
                        if (chr.equals("")) {
                            String[] lineSplit = line.split(" ");
                            boolean chrFound = false;
                            for (String s: lineSplit) {
                            	if (!s.trim().equals("")) {
                                    if (chrFound) {
                            		chr = s.trim();
                                        //System.err.println("--- chr: " + chr);
                                        break;
                                    }
                                    if (s.trim().toLowerCase().contains("chromosome"))
                            		chrFound = true;
                            	}                            
                            }
                        }
                    }
                    else if (line.trim().toLowerCase().contains("from")) {
                        if (start == -1) {
                            String[] lineSplit = line.split(" ");
                            String lastString = lineSplit[lineSplit.length-1];
                            start = Integer.valueOf(lastString.substring(0, lastString.length()-1));
                            //System.err.println("--- start: " + start);
                        }
                    }
                    else if (line.trim().toLowerCase().contains("to")) {
                        if (end == -1) {
                            String[] lineSplit = line.split(" ");
                            String lastString = lineSplit[lineSplit.length-1];
                            end = Integer.valueOf(lastString.substring(0, lastString.length()-1));
                            //System.err.println("--- end: " + end);
                        }
                    }
                    else if (line.trim().toLowerCase().contains("strand")) {
                        if (strand.equals("")) {
                            String[] lineSplit = line.split(" ");
                            String lastString = lineSplit[lineSplit.length-1];
                            String strand_ = lastString.substring(0, lastString.length()-1);
                            if (strand_.trim().toLowerCase().equals("minus"))
                                strand = "-";
                            else if (strand_.trim().toLowerCase().equals("plus"))
                                strand = "+";
                            //System.err.println("--- strand: " + strand);
                        }
                    }
                }
                line = reader.readLine();
            }
            reader.close();
            ncbiBiotab_tmp.delete();
            
            //System.err.println();
            
            if (!result_tmp.isEmpty())
                return result_tmp;

            if (!chr.equals("") && (start > 0 && end > 0)) {
                result.put("CHR", chr);
                result.put("START", String.valueOf(start+1)); // +1 : NCBI is 0-based
                result.put("END", String.valueOf(end+1)); // +1 : NCBI is 0-based
                result.put("STRAND", strand);
                result.put("GDC_ENTREZ", entrez);
                result.put("GDC_SYMBOL", gene);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
}
