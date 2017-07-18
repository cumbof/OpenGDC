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
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import opengdc.Settings;

/**
 *
 * @author fabio
 */
public class Ensembl {
    
    private static String ensembl_table_path = Settings.getENSEMBLDataPath();
    private static HashMap<String, ArrayList<HashMap<String, String>>> ensembl_data_gene = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> ensembl_data_exon = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> ensembl_data_transcript = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> ensembl_data_utr = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> ensembl_data_cds = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> ensembl_data_start_codon = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> ensembl_data_stop_codon = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> ensembl_data_selenocysteine = new HashMap<>();
	
    private static void loadEnsemblTableByType(String type) {
	try {
            InputStream fstream = new FileInputStream(ensembl_table_path);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.startsWith("#") && !line.trim().equals("")) {
                    	HashMap<String, String> entry = new HashMap<>();
                    	String[] arr = line.split("\t");
                        
                        String line_type = arr[2].trim();
                        if (line_type.trim().toLowerCase().equals(type.trim().toLowerCase())) {
                            entry.put("CHR", arr[0].trim());
                            entry.put("START", arr[3].trim());
                            entry.put("END", arr[4].trim());
                            entry.put("STRAND", arr[6].trim());
                            entry.put("TYPE", arr[2].trim());
                            String symbol = "NA";
                            String ensembl = "NA";
                            String[] extendedInfo_arr = arr[8].trim().split(";");
                            for (String data: extendedInfo_arr) {
                                if (data.toLowerCase().trim().startsWith("gene_name")) {
                                    //String[] name_split = data.split("="); // for gff3
                                    String[] name_split = data.split("\""); // for gtf
                                    symbol = name_split[name_split.length-1];
                                }
                                else if (data.toLowerCase().trim().startsWith("gene_id")) {
                                    //String[] id_split = data.split("="); // for gff3
                                    String[] id_split = data.split("\""); //for gtf
                                    ensembl = id_split[id_split.length-1];
                                }
                            }
                            entry.put("SYMBOL", symbol);
                            if (!ensembl.equals("NA")) {
                                ArrayList<HashMap<String, String>> entries = new ArrayList<>();
                                if (type.toLowerCase().trim().equals("gene")) {
                                    if (ensembl_data_gene.containsKey(ensembl))
                                        entries = ensembl_data_gene.get(ensembl);
                                    entries.add(entry);
                                    ensembl_data_gene.put(ensembl, entries);
                                }
                                else if (type.toLowerCase().trim().equals("exon")) {
                                    if (ensembl_data_exon.containsKey(ensembl))
                                        entries = ensembl_data_exon.get(ensembl);
                                    entries.add(entry);
                                    ensembl_data_exon.put(ensembl, entries);
                                }
                                else if (type.toLowerCase().trim().equals("transcript")) {
                                    if (ensembl_data_transcript.containsKey(ensembl))
                                        entries = ensembl_data_transcript.get(ensembl);
                                    entries.add(entry);
                                    ensembl_data_transcript.put(ensembl, entries);
                                }
                                else if (type.toLowerCase().trim().equals("utr")) {
                                    if (ensembl_data_utr.containsKey(ensembl))
                                        entries = ensembl_data_utr.get(ensembl);
                                    entries.add(entry);
                                    ensembl_data_utr.put(ensembl, entries);
                                }
                                else if (type.toLowerCase().trim().equals("cds")) {
                                    if (ensembl_data_cds.containsKey(ensembl))
                                        entries = ensembl_data_cds.get(ensembl);
                                    entries.add(entry);
                                    ensembl_data_cds.put(ensembl, entries);
                                }
                                else if (type.toLowerCase().trim().equals("start_codon")) {
                                    if (ensembl_data_start_codon.containsKey(ensembl))
                                        entries = ensembl_data_start_codon.get(ensembl);
                                    entries.add(entry);
                                    ensembl_data_start_codon.put(ensembl, entries);
                                }
                                else if (type.toLowerCase().trim().equals("stop_codon")) {
                                    if (ensembl_data_stop_codon.containsKey(ensembl))
                                        entries = ensembl_data_stop_codon.get(ensembl);
                                    entries.add(entry);
                                    ensembl_data_stop_codon.put(ensembl, entries);
                                }
                                else if (type.toLowerCase().trim().equals("selenocysteine")) {
                                    if (ensembl_data_selenocysteine.containsKey(ensembl))
                                        entries = ensembl_data_selenocysteine.get(ensembl);
                                    entries.add(entry);
                                    ensembl_data_selenocysteine.put(ensembl, entries);
                                }
                            }
                        }
                    }
                } catch (Exception e) { /* IF THROWS ERROR -> SKIP LINE */ }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
        
    public static ArrayList<HashMap<String, String>> extractEnsemblInfo(String ensembl_id, String type) {
    	if (type.toLowerCase().trim().equals("gene") && ensembl_data_gene.isEmpty()) loadEnsemblTableByType(type);
        else if (type.toLowerCase().trim().equals("exon") && ensembl_data_exon.isEmpty()) loadEnsemblTableByType(type);
        else if (type.toLowerCase().trim().equals("transcript") && ensembl_data_transcript.isEmpty()) loadEnsemblTableByType(type);
        else if (type.toLowerCase().trim().equals("utr") && ensembl_data_utr.isEmpty()) loadEnsemblTableByType(type);
        else if (type.toLowerCase().trim().equals("cds") && ensembl_data_cds.isEmpty()) loadEnsemblTableByType(type);
        else if (type.toLowerCase().trim().equals("start_codon") && ensembl_data_start_codon.isEmpty()) loadEnsemblTableByType(type);
        else if (type.toLowerCase().trim().equals("stop_codon") && ensembl_data_stop_codon.isEmpty()) loadEnsemblTableByType(type);
        else if (type.toLowerCase().trim().equals("selenocysteine") && ensembl_data_selenocysteine.isEmpty()) loadEnsemblTableByType(type);
        
        if (type.toLowerCase().trim().equals("gene")) { if (ensembl_data_gene.containsKey(ensembl_id)) return ensembl_data_gene.get(ensembl_id); }
        if (type.toLowerCase().trim().equals("exon")) { if (ensembl_data_exon.containsKey(ensembl_id)) return ensembl_data_exon.get(ensembl_id); }
        if (type.toLowerCase().trim().equals("transcript")) { if (ensembl_data_transcript.containsKey(ensembl_id)) return ensembl_data_transcript.get(ensembl_id); }
        if (type.toLowerCase().trim().equals("utr")) { if (ensembl_data_utr.containsKey(ensembl_id)) return ensembl_data_utr.get(ensembl_id); }
        if (type.toLowerCase().trim().equals("cds")) { if (ensembl_data_cds.containsKey(ensembl_id)) return ensembl_data_cds.get(ensembl_id); }
        if (type.toLowerCase().trim().equals("start_codon")) { if (ensembl_data_start_codon.containsKey(ensembl_id)) return ensembl_data_start_codon.get(ensembl_id); }
        if (type.toLowerCase().trim().equals("stop_codon")) { if (ensembl_data_stop_codon.containsKey(ensembl_id)) return ensembl_data_stop_codon.get(ensembl_id); }
        if (type.toLowerCase().trim().equals("selenocysteine")) { if (ensembl_data_selenocysteine.containsKey(ensembl_id)) return ensembl_data_selenocysteine.get(ensembl_id); }
        
        return new ArrayList<>();
    }

}
