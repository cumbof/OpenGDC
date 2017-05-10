/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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

    public static boolean updateNCBIData(String gdc_entrez, String gdc_gene, String ncbi_entrez, String ncbi_gene, String chr, String start, String end, String strand) {
        File ncbidata = new File(Settings.getNCBIDataPath());
        if (ncbidata.exists()) {
            try {
                BufferedWriter output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(Settings.getNCBIDataPath(), true), "UTF-8"));
                output.append(gdc_entrez + "\t" + gdc_gene + "\t" + ncbi_entrez + "\t" + ncbi_gene + "\t" + chr + "\t" + start + "\t" + end + "\t" + strand);
                output.newLine();
                output.close();
            } catch (Exception e) {
                return false;
            }
        }
        loadNCBIData(true);
        return true;
    }

    public static HashMap<String, HashMap<String, String>> loadNCBIData(boolean forceLoad) {
        if (ncbi_data.isEmpty() || forceLoad) {
            ncbi_data = new HashMap<>();
            try {
                InputStream fstream = new FileInputStream(Settings.getNCBIDataPath());
                DataInputStream in = new DataInputStream(fstream);
                BufferedReader br = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        String[] arr = line.split("\t");
                        HashMap<String, String> info = new HashMap<>();
                        info.put("STRAND", arr[7]);
                        info.put("START", arr[5]);
                        info.put("END", arr[6]);
                        info.put("CHR", arr[4]);
                        info.put("NCBI_GENE", arr[3]);
                        info.put("NCBI_ENTREZ", arr[2]);
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
    
    public static HashMap<String, String> getGeneInfo(String entrez) {
        HashMap<String, HashMap<String, String>> data = loadNCBIData(false);
        if (data.containsKey(entrez))
            return data.get(entrez);
        return null;
    }

    public static HashMap<String, String> retrieveGenomicCoordinates(String entrez, String gene) {
        try {
            HashMap<String, String> result = new HashMap<>();
            HashMap<String, String> result_tmp = new HashMap<String, String>();
            
            String strand = "";
            String chr = "";
            int start = -1;
            int end = -1;

            //String ncbiBiotabQuery = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi/?db=Gene&id=" + entrez + "&format=biotab";
            String ncbiBiotabQuery = "http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi/?db=Gene&id=" + entrez;
            File ncbiBiotab_tmp = new File(Settings.getTmpDir()+"ncbi.efetch.fcgi.txt");
            DownloadUtils.downloadDataFromUrl(ncbiBiotabQuery, ncbiBiotab_tmp.getAbsolutePath(), 0);
            BufferedReader reader = new BufferedReader(new FileReader(ncbiBiotab_tmp.getAbsolutePath()));
            String line = reader.readLine();
            
            boolean genomeMatch = false; // match on GRCh38 genome version
            
            boolean replaced = false;
            boolean db_GeneID = false;
            
            String newGene = "";
            String newEntrez = "";
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
                            newEntrez = line_split[line_split.length-1];
                            result_tmp = retrieveGenomicCoordinates(newEntrez, gene);
                            result_tmp.put("GDC_ENTREZ", newEntrez);
                            break;
                        }
                    }
                }
                ////////////////////////////////////////////////
                if (line.trim().toLowerCase().contains("locus")) {
                    if (newGene.equals("")) {
                        String[] lineSplit = line.split(" ");
                        String lastString = lineSplit[lineSplit.length-1].replaceAll("\"", "");
                        newGene = lastString.substring(0, lastString.length()-1);
                        //System.err.println("--- newGene: " + newGene);
                    }
                }
                else if (line.trim().toLowerCase().contains("geneid")) {
                    if (newEntrez.equals("")) {
                        String[] lineSplit = line.split(" ");
                        String lastString = lineSplit[lineSplit.length-1];
                        newEntrez = lastString.substring(0, lastString.length()-1);
                        //System.err.println("--- newEntrez: " + newEntrez);
                    }
                }
                    
                if (genomeMatch) {
                    if (line.trim().toLowerCase().contains("label") && line.trim().toLowerCase().contains("chromosome")) {
                        if (chr.equals("")) {
                            String[] lineSplit = line.split(" ");
                            String lastString = lineSplit[lineSplit.length-1].replaceAll("\"", "");
                            chr = lastString.substring(0, lastString.length()-1);
                            //System.err.println("--- chr: " + chr);
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
                result.put("NCBI_ENTREZ", newEntrez);
                result.put("NCBI_GENE", newGene);
            }
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }
    
}
