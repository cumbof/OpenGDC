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
public class Gencode {
    
    private static String gencode_table_path = Settings.getGENCODEDataPath();
    private static HashMap<String, ArrayList<HashMap<String, String>>> gencode_data_gene = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> gencode_data_exon = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> gencode_data_transcript = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> gencode_data_utr = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> gencode_data_cds = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> gencode_data_start_codon = new HashMap<>();
    private static HashMap<String, ArrayList<HashMap<String, String>>> gencode_data_stop_codon = new HashMap<>();

    
    private static void loadGencodeTableByType(String identifierName, String type) {
	try {
            InputStream fstream = new FileInputStream(gencode_table_path);
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
                            String symbol_lower = "NA";
                            String ensembl = "NA";
                            String ensembl_id_noversion = "NA";
                            String identifier = "NA";
                            String[] extendedInfo_arr = arr[8].trim().split(";");
                            for (String data: extendedInfo_arr) {
                                if (data.toLowerCase().trim().startsWith("gene_name")) {
                                    //String[] name_split = data.split("="); // for gff3
                                    String[] name_split = data.split("\""); // for gtf
                                    symbol = name_split[name_split.length-1];
                                    symbol_lower = symbol.toLowerCase();

                                }
                                else if (data.toLowerCase().trim().startsWith("gene_id")) {
                                    //String[] id_split = data.split("="); // for gff3
                                    String[] id_split = data.split("\""); //for gtf
                                    ensembl = id_split[id_split.length-1];
				    ensembl_id_noversion = ensembl.split("\\.")[0];
                                }
                            }
                            
                            if(identifierName.toLowerCase().trim().equals("symbol"))
                            	identifier = symbol_lower;
                            else if (identifierName.toLowerCase().trim().equals("ensembl_id"))
                            	identifier = ensembl_id_noversion;
                            
                            entry.put("SYMBOL", symbol);
                            entry.put("ENSEMBL_ID", ensembl_id_noversion);
                            if (!identifier.equals("NA")) {
                                ArrayList<HashMap<String, String>> entries = new ArrayList<>();
                                if (type.toLowerCase().trim().equals("gene")) {
                                    if (gencode_data_gene.containsKey(identifier))
                                        entries = gencode_data_gene.get(identifier);
                                    entries.add(entry);
                                    gencode_data_gene.put(identifier, entries);
                                }
                                else if (type.toLowerCase().trim().equals("exon")) {
                                    if (gencode_data_exon.containsKey(identifier))
                                        entries = gencode_data_exon.get(identifier);
                                    entries.add(entry);
                                    gencode_data_exon.put(identifier, entries);
                                }
                                else if (type.toLowerCase().trim().equals("transcript")) {
                                    if (gencode_data_transcript.containsKey(identifier))
                                        entries = gencode_data_transcript.get(identifier);
                                    entries.add(entry);
                                    gencode_data_transcript.put(identifier, entries);
                                }
                                else if (type.toLowerCase().trim().equals("utr")) {
                                    if (gencode_data_utr.containsKey(identifier))
                                        entries = gencode_data_utr.get(identifier);
                                    entries.add(entry);
                                    gencode_data_utr.put(identifier, entries);
                                }
                                else if (type.toLowerCase().trim().equals("cds")) {
                                    if (gencode_data_cds.containsKey(identifier))
                                        entries = gencode_data_cds.get(identifier);
                                    entries.add(entry);
                                    gencode_data_cds.put(identifier, entries);
                                }
                                else if (type.toLowerCase().trim().equals("start_codon")) {
                                    if (gencode_data_start_codon.containsKey(identifier))
                                        entries = gencode_data_start_codon.get(identifier);
                                    entries.add(entry);
                                    gencode_data_start_codon.put(identifier, entries);
                                }
                                else if (type.toLowerCase().trim().equals("stop_codon")) {
                                    if (gencode_data_stop_codon.containsKey(identifier))
                                        entries = gencode_data_stop_codon.get(identifier);
                                    entries.add(entry);
                                    gencode_data_stop_codon.put(identifier, entries);
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
        
    public static ArrayList<HashMap<String, String>> extractGencodeInfo(String identifierName, String identifier, String type) {
    	if (type.toLowerCase().trim().equals("gene") && gencode_data_gene.isEmpty()) loadGencodeTableByType(identifierName,type);
        else if (type.toLowerCase().trim().equals("exon") && gencode_data_exon.isEmpty()) loadGencodeTableByType(identifierName,type);
        else if (type.toLowerCase().trim().equals("transcript") && gencode_data_transcript.isEmpty()) loadGencodeTableByType(identifierName,type);
        else if (type.toLowerCase().trim().equals("utr") && gencode_data_utr.isEmpty()) loadGencodeTableByType(identifierName,type);
        else if (type.toLowerCase().trim().equals("cds") && gencode_data_cds.isEmpty()) loadGencodeTableByType(identifierName,type);
        else if (type.toLowerCase().trim().equals("start_codon") && gencode_data_start_codon.isEmpty()) loadGencodeTableByType(identifierName,type);
        else if (type.toLowerCase().trim().equals("stop_codon") && gencode_data_stop_codon.isEmpty()) loadGencodeTableByType(identifierName,type);
        
        if(identifierName.toLowerCase().trim().equals("symbol")) {identifier = identifier.toLowerCase(); }
        if (type.toLowerCase().trim().equals("gene")) { if (gencode_data_gene.containsKey(identifier)) return gencode_data_gene.get(identifier); }
        if (type.toLowerCase().trim().equals("exon")) { if (gencode_data_exon.containsKey(identifier)) return gencode_data_exon.get(identifier); }
        if (type.toLowerCase().trim().equals("transcript")) { if (gencode_data_transcript.containsKey(identifier)) return gencode_data_transcript.get(identifier); }
        if (type.toLowerCase().trim().equals("utr")) { if (gencode_data_utr.containsKey(identifier)) return gencode_data_utr.get(identifier); }
        if (type.toLowerCase().trim().equals("cds")) { if (gencode_data_cds.containsKey(identifier)) return gencode_data_cds.get(identifier); }
        if (type.toLowerCase().trim().equals("start_codon")) { if (gencode_data_start_codon.containsKey(identifier)) return gencode_data_start_codon.get(identifier); }
        if (type.toLowerCase().trim().equals("stop_codon")) { if (gencode_data_stop_codon.containsKey(identifier)) return gencode_data_stop_codon.get(identifier); }
        
        return new ArrayList<>();
    }

}
