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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import opengdc.Settings;

/**
 *
 * @author fabio
 */
public class NCBI {
    
    private static final String GENOME_VERSION = "grch38";
    private static HashMap<String, HashMap<String, String>> ncbi_data = new HashMap<>();
    
    public static boolean updateNCBIData(String gdc_entrez, String gdc_gene, String chr, String start, String end, String strand) {
        HashMap<String, String> info = new HashMap<>();
        info.put("STRAND", strand);
        info.put("START", start);
        info.put("END", end);
        info.put("CHR", chr);
        info.put("GDC_SYMBOL", gdc_gene);
        info.put("GDC_ENTREZ", gdc_entrez);
        ncbi_data.put(gdc_gene, info);
        return true;
    }
    
    public static HashMap<String, String> extractNCBIinfo(String chr, String gene_symbol_comp, String start_site, String end_site, String gene_type_comp, String transcript_id_comp, String position_to_tss_comp) {
        HashMap<String, String> result = new HashMap<>();
        
        HashMap<String, Integer> gene2CpGdistance = new HashMap<>();
        HashMap<String, String> gene2startEnd = new HashMap<>();
        
        String transcript = ""; 
        String position_to_TSS = ""; 
        String gene_type = "";
        String gene_symbol = "";
        String strand = "";
        String entrez = "";
        String all_entrez_ids = "";
        String all_gene_symbols = "";
        String all_gene_types = "";
        String all_transcript_ids = ""; 
        String all_positions_to_TSS = "";
        
        String[] genes = gene_symbol_comp.split(";");
        
        int i = 0;
        while (i < genes.length) {
            String gene_symbol_tmp = genes[i];
            int last = getLastIndex(genes,i);

            if (ncbi_data.containsKey(gene_symbol_tmp)) { 
                String start = ncbi_data.get(gene_symbol_tmp).get("START");
                String end = ncbi_data.get(gene_symbol_tmp).get("END");
                int startf = Integer.parseInt(start);
                int s_site = Integer.parseInt(start_site);
                int endf = Integer.parseInt(end);
                int e_site = Integer.parseInt(end_site);

                if(s_site>= startf && endf>=e_site){
                    int distance= (s_site-startf)+(endf-e_site);
                    gene2CpGdistance.put(gene_symbol_tmp,distance);
                }
            }
            else
                gene2CpGdistance = isInCpGsite(chr, gene_symbol_tmp, start_site, end_site, gene2CpGdistance) ;

            if (ncbi_data.containsKey(gene_symbol_tmp))
                entrez = ncbi_data.get(gene_symbol_tmp).get("GDC_ENTREZ"); //in ncbi_data map there are all gene symbols and entrez ids.
            else
                entrez = "null";

            all_entrez_ids = all_entrez_ids +";"+entrez ;
            all_gene_symbols = all_gene_symbols +";"+gene_symbol_tmp;

            gene_type = gene_type_comp.split(";")[i];
            all_gene_types = all_gene_types + ";"+gene_type;

            gene2startEnd.put(gene_symbol_tmp, i+"_"+last);

            i=last;
        }
        
        //finding in map "gene distance_CpGsite" the gene at min distance => gene_symbol
        //if CpG site does not fall into any genomic region of the genes, then gene2CpGdistance is empty
        //and the fields gene_symbol, entrez_id, gene_type, transcript_id, position_to_tss are BLANK.
        
        if (!gene2CpGdistance.keySet().isEmpty()) {
            gene_symbol = getMinDistanceformCpGsite(gene2CpGdistance);
            String start_end = gene2startEnd.get(gene_symbol);

            int index_start = Integer.parseInt(start_end.split("_")[0]);
            int index_end = Integer.parseInt(start_end.split("_")[1]);

            for(int z =index_start;z<index_end;z++){
                transcript = (transcript+"|"+transcript_id_comp.split(";")[z]);
                position_to_TSS = (position_to_TSS+"|"+position_to_tss_comp.split(";")[z]);
            }
            
            transcript=transcript.substring(1); 
            position_to_TSS=position_to_TSS.substring(1);
            gene_type = gene_type_comp.split(";")[index_start];
            strand = ncbi_data.get(gene_symbol).get("STRAND");
            entrez = ncbi_data.get(gene_symbol).get("GDC_ENTREZ");
        }
        else {
            entrez = "null";
            gene_type = "";
            strand = "*";
            position_to_TSS = "null";
        }

        for (String gene: gene2startEnd.keySet()) {
            String start_end = gene2startEnd.get(gene);
            int index_start = Integer.parseInt(start_end.split("_")[0]);
            int index_end = Integer.parseInt(start_end.split("_")[1]);
            String transcript_tmp= "";
            String position_to_TSS_tmp = "";
            
            for (int z =index_start; z<index_end; z++) {
                transcript_tmp = (transcript_tmp+"|"+transcript_id_comp.split(";")[z]);
                position_to_TSS_tmp = (position_to_TSS_tmp+"|"+position_to_tss_comp.split(";")[z]);
            }
            
            all_transcript_ids=all_transcript_ids+";"+transcript_tmp.substring(1); 
            all_positions_to_TSS=all_positions_to_TSS+";"+position_to_TSS_tmp.substring(1);
        }

        result.put("GENE_TYPES", all_gene_types.substring(1)); 
        result.put("GENE_SYMBOLS", all_gene_symbols.substring(1));
        result.put("ENTREZ_IDs", all_entrez_ids.substring(1));
        result.put("TRANSCRIPT_IDS", all_transcript_ids.substring(1)); 
        result.put("POSITIONS_TO_TSS", all_positions_to_TSS.substring(1)); 
        result.put("TRANSCRIPT_ID", transcript); 
        result.put("POSITION_TO_TSS", position_to_TSS); 
        result.put("GENE_TYPE", gene_type); 
        result.put("STRAND", strand);
        result.put("SYMBOL", gene_symbol);
        result.put("ENTREZ", entrez);       

        return result;
    }
    
    //extracting gene related to the CpG site at min distance 
    private static String getMinDistanceformCpGsite(HashMap<String, Integer> gene2CpGdistance) {
        ArrayList<String> genes_list = new ArrayList<>(gene2CpGdistance.keySet());
        String gene_symbol = genes_list.get(0);
        int min = gene2CpGdistance.get(gene_symbol);

        for(String gene: genes_list){
            int distance = gene2CpGdistance.get(gene);
            if(min > distance){
                min = distance;
                gene_symbol = gene;
            }
        }
        return gene_symbol;
    }
    
    private static String getEntrezFromNCBIline(String arr) {
        String entrez = "";
        for (String data: arr.split(",")) {
            if (data.toLowerCase().trim().contains("geneid")) {
                String[] name_split = data.split(":");
                entrez = name_split[name_split.length-1];
                break;
            }
        }
        return entrez;
    }
    
    public static HashMap<String, Integer> isInCpGsite(String chr, String gene_symbol, String start_site, String end_site, HashMap<String, Integer> gene2CpGdistance) {
        try {
            InputStream fstream = new FileInputStream(Settings.getNCBIDataPath());
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            boolean found = false;
            while ((line = br.readLine()) != null && !found) {
                try {
                    if (!line.startsWith("#") && !line.equals("")) {
                        String[] arr = line.split("\t");
                        String extendedInfo = arr[8];
                        int start = Integer.parseInt(arr[3]);
                        int s_site = Integer.parseInt(start_site);
                        int end = Integer.parseInt(arr[4]);
                        int e_site = Integer.parseInt(end_site);
                        if (extendedInfo.contains("Name=")) {
                            String[] extendedInfo_arr = extendedInfo.split(";");
                            for (String data: extendedInfo_arr) {
                                if (data.toLowerCase().trim().startsWith("name")) {
                                    String[] name_split = data.split("=");
                                    String symbol = name_split[name_split.length-1];
                                    if(gene_symbol.equals(symbol)){
                                        if((s_site>= start && end>=e_site)){
                                            // if CpG island is in the current interval -> put in map
                                            int distance = (s_site-start)+(end-e_site);
                                            gene2CpGdistance.put(gene_symbol, distance);
                                        }
                                        String entrez_tmp = getEntrezFromNCBIline(extendedInfo);
                                        updateNCBIData(entrez_tmp, symbol, chr, arr[3], arr[4], arr[6]);
                                        found = true;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {}
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  gene2CpGdistance;
    }
    
    public static int getLastIndex(String[] genes, int i) {
        int j = i+1;
        while(j< genes.length && genes[j].equals(genes[i])){
                j ++;
        }
        return j;
    }
    
    
    /*************************************************************************************************************/
    /************************************************ OLD METHOD: ************************************************/
    /*********************************************** QUERYING NCBI ***********************************************/
    /*************************************************************************************************************/
    
    
    /*private static final String GENOME_VERSION = "grch38";
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
    }*/
    
}
