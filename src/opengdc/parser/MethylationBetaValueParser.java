/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
 */
package opengdc.parser;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import opengdc.GUI;
import opengdc.integration.GeneNames;
import opengdc.integration.Gencode;
import opengdc.util.FSUtils;
import opengdc.util.FormatUtils;
import opengdc.util.GDCQuery;

/**
 *
 * @author fabio
 */
public class MethylationBetaValueParser extends BioParser {

    @Override
    public int convert(String program, String disease, String dataType, String inPath, String outPath) {
        int acceptedFiles = FSUtils.acceptedFilesInFolder(inPath, getAcceptedInputFileFormats());
        System.err.println("Data Amount: " + acceptedFiles + " files" + "\n\n");
        GUI.appendLog("Data Amount: " + acceptedFiles + " files" + "\n\n");
        
        if (acceptedFiles == 0)
            return 1;
        
        HashSet<String> filesPathConverted = new HashSet<>();
        
        File[] files = (new File(inPath)).listFiles();
        for (File f: files) {
            if (f.isFile()) {
                String extension = FSUtils.getFileExtension(f);
                if (getAcceptedInputFileFormats().contains(extension)) {
                    System.err.println("Processing " + f.getName());
                    GUI.appendLog("Processing " + f.getName() + "\n");
                    
                    String file_uuid = f.getName().split("_")[0];
                    HashSet<String> attributes = new HashSet<>();
                    attributes.add("aliquot_id");
                    HashMap<String, String> file_info = GDCQuery.retrieveExpInfoFromAttribute("files.file_id", file_uuid, attributes);
                    String aliquot_uuid = "";
                    if (file_info != null)
                        if (file_info.containsKey("aliquot_id"))
                            aliquot_uuid = file_info.get("aliquot_id");
                    
                    if (!aliquot_uuid.trim().equals("")) {
                        try {
                            Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.initDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.CREATE);

                            InputStream fstream = new FileInputStream(f.getAbsolutePath());
                            DataInputStream in = new DataInputStream(fstream);
                            BufferedReader br = new BufferedReader(new InputStreamReader(in));
                            String line;
                            boolean firstLine = true;
                            while ((line = br.readLine()) != null) {
                                if (firstLine)
                                    firstLine = false; // just skip the first line (header)
                                else {
                                    String[] line_split = line.split("\t");
                                    String chr = line_split[2]; // 1
                                    String start = line_split[3]; //2
                                    String end = line_split[4]; //3
                                    String strand = "*"; //4
                                    String composite_element_ref = line_split[0]; //5
                                    String beta_value = line_split[1]; //6
                                    String gene_symbols_comp = line_split[5]; 
                                    String gene_types_comp = line_split[6];
                                    String transcript_ids_comp = line_split[7];
                                    String positions_to_tss_comp = line_split[8];

                                    String all_gene_symbols = ""; //12
                                    String all_entrez_ids = "null"; //13
                                    String all_gene_types = ""; //14
                                    String all_transcript_ids = ""; //15
                                    String all_positions_to_tss = "null"; //16

                                    String cgi_coordinate = line_split[9]; //17
                                    String feature_type = line_split[10]; //18

                                    String gene_symbol = ""; //7
                                    String entrez_id = ""; //8
                                    String gene_type = ""; //9
                                    String transcript_id = ""; //10
                                    String position_to_tss = ""; //11
                                    
                                    if (!chr.equals("*")) {
                                        if (!gene_symbols_comp.isEmpty()) {
                                            HashMap<String, String> fields = extractFields(chr, gene_symbols_comp, start,end , gene_types_comp, transcript_ids_comp, positions_to_tss_comp);
                                            strand = fields.get("STRAND");
                                            gene_symbol = fields.get("SYMBOL");
                                            gene_type = fields.get("GENE_TYPE");
                                            transcript_id = fields.get("TRANSCRIPT_ID");
                                            position_to_tss = fields.get("POSITION_TO_TSS");			
                                            entrez_id = fields.get("ENTREZ");
                                            all_entrez_ids = fields.get("ENTREZ_IDs");
                                            all_gene_symbols = fields.get("GENE_SYMBOLS");
                                            all_gene_types = fields.get("GENE_TYPES");
                                            all_transcript_ids = fields.get("TRANSCRIPT_IDS");
                                            all_positions_to_tss = fields.get("POSITIONS_TO_TSS");

                                            ArrayList<String> values = new ArrayList<>();
                                            values.add(parseValue(chr, 0));
                                            values.add(parseValue(start, 1));
                                            values.add(parseValue(end, 2));
                                            values.add(parseValue(strand, 3));
                                            values.add(parseValue(composite_element_ref, 4));
                                            values.add(parseValue(beta_value, 5));
                                            values.add(parseValue(gene_symbol, 6));
                                            values.add(parseValue(entrez_id, 7));
                                            values.add(parseValue(gene_type, 8));
                                            values.add(parseValue(transcript_id, 9));
                                            values.add(parseValue(position_to_tss, 10));
                                            values.add(parseValue(all_gene_symbols, 11));
                                            values.add(parseValue(all_entrez_ids, 12));
                                            values.add(parseValue(all_gene_types, 13));
                                            values.add(parseValue(all_transcript_ids, 14));
                                            values.add(parseValue(all_positions_to_tss, 15));
                                            values.add(parseValue(cgi_coordinate, 16));
                                            values.add(parseValue(feature_type, 17));

                                            Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.createEntry(this.getFormat(), values, getHeader())).getBytes("UTF-8"), StandardOpenOption.APPEND);
                                        }
                                    }
                                }
                            }
                            br.close();
                            in.close();
                            fstream.close();

                            Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.endDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.APPEND);
                            filesPathConverted.add(outPath + file_uuid + "." + this.getFormat());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        System.err.println("ERROR: an error has occurred while retrieving the aliquot UUID for :" + file_uuid);
                        GUI.appendLog("ERROR: an error has occurred while retrieving the aliquot UUID for :" + file_uuid);
                    }
                }
            }
        }
        
        if (!filesPathConverted.isEmpty()) {
            // write header.schema
            try {
                System.err.println("\n" + "Generating header.schema");
                GUI.appendLog("\n" + "Generating header.schema" + "\n");
                Files.write((new File(outPath + "header.schema")).toPath(), (FormatUtils.generateDataSchema(this.getHeader(), this.getAttributesType())).getBytes("UTF-8"), StandardOpenOption.CREATE);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public static HashMap<String, String> extractFields(String chr, String gene_symbol_comp, String start_site, String end_site, String gene_type_comp, String transcript_id_comp, String position_to_tss_comp) {
        HashMap<String, String> result = new HashMap<>();
        HashMap<String, Integer> gene2CpGdistance = new HashMap<>();
        HashMap<String, List<String>> gene2startEnd = new HashMap<>();

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

            ArrayList<HashMap<String, String>> gencode_info = Gencode.extractGencodeInfo("symbol", gene_symbol_tmp, "gene");
            if (!gencode_info.isEmpty()) {

                HashMap<String, String> gene_info = gencode_info.get(0);

                if(!gene_info.isEmpty()){

                    String start = gene_info.get("START");
                    String end = gene_info.get("END");
                    int startf = Integer.parseInt(start);
                    int s_site = Integer.parseInt(start_site);
                    int endf = Integer.parseInt(end);
                    int e_site = Integer.parseInt(end_site);

                    if(s_site>= startf && endf>=e_site){
                        int distance= (s_site-startf)+(endf-e_site);
                        gene2CpGdistance.put(gene_symbol_tmp,distance);
                    }
                    String entrez_tmp = GeneNames.getEntrezFromSymbol(gene_symbol_tmp);
                    if (entrez_tmp != null)
                        entrez = entrez_tmp;
                    else {
                        String ensembl_id = gene_info.get("ENSEMBL_ID");
                        entrez = GeneNames.getEntrezFromEnsemblID(ensembl_id);
                    }
                }
            }
            else
                entrez = "null";

            all_entrez_ids = all_entrez_ids +";"+entrez ;
            all_gene_symbols = all_gene_symbols +";"+gene_symbol_tmp;

            gene_type = gene_type_comp.split(";")[i];
            all_gene_types = all_gene_types + ";"+gene_type;

            List<String> index_entrez = new ArrayList<String>();
            index_entrez.add(i+"_"+last);
            index_entrez.add(entrez);
            gene2startEnd.put(gene_symbol_tmp, index_entrez);

            i=last;
        }

        //finding in map "gene distance_CpGsite" the gene at min distance => gene_symbol
        //if CpG site does not fall into any genomic region of the genes, then gene2CpGdistance is empty
        //and the fields gene_symbol, entrez_id, gene_type, transcript_id, position_to_tss are BLANK.
        if (!gene2CpGdistance.keySet().isEmpty()) {
            gene_symbol = getMinDistanceformCpGsite(gene2CpGdistance);
            //String start_end = gene2startEnd.get(gene_symbol);
            List<String> start_endentrezId = gene2startEnd.get(gene_symbol);
            String start_end = start_endentrezId.get(0);
            
            int index_start = Integer.parseInt(start_end.split("_")[0]);
            int index_end = Integer.parseInt(start_end.split("_")[1]);

            for(int z =index_start;z<index_end;z++){
                transcript = (transcript+"|"+transcript_id_comp.split(";")[z]);
                position_to_TSS = (position_to_TSS+"|"+position_to_tss_comp.split(";")[z]);
            }

            transcript=transcript.substring(1); 
            position_to_TSS=position_to_TSS.substring(1);
            gene_type = gene_type_comp.split(";")[index_start];
            ArrayList<HashMap<String, String>> gencode_info = Gencode.extractGencodeInfo("symbol", gene_symbol, "gene");
            HashMap<String, String> gene_info = gencode_info.get(0);
            strand = gene_info.get("STRAND");
            entrez = start_endentrezId.get(1);
        }
        else {
            entrez = "null";
            gene_type = "";
            strand = "*";
            position_to_TSS = "null";
        }

        for (String gene: gene2startEnd.keySet()) {
            List<String> start_endentrezId = gene2startEnd.get(gene);
            String start_end = start_endentrezId.get(0);
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
        result.put("ENTREZ_IDs", all_entrez_ids.substring(1).replaceAll("null", ""));
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

    public static int getLastIndex(String[] genes, int i) {
        int j = i+1;
        while(j< genes.length && genes[j].equals(genes[i])){
            j ++;
        }
        return j;
    }

    @Override
    public String[] getHeader() {
        String[] header = new String[17];
        header[0] = "chr";
        header[1] = "start";
        header[2] = "stop";
        header[3] = "strand";
        header[4] = "composite_element_ref";
        header[5] = "beta_value";
        header[6] = "gene_symbol";
        header[7] = "entrez_gene_id";
        header[8] = "gene_type";
        header[9] = "transcript_id";
        header[10] = "position_to_tss";
        header[11] ="all_gene_symbols";
        header[12] ="all_entrez_gene_ids";
        header[13] ="all_transcript_ids";
        header[14] ="all_positions_to_tss";
        header[15] = "cgi_coordinate";
        header[16] = "feature_type";
        return header;
    }

    @Override
    public String[] getAttributesType() {
        String[] attr_type = new String[17];
        attr_type[0] = "STRING";
        attr_type[1] = "LONG";
        attr_type[2] = "LONG";
        attr_type[3] = "CHAR";
        attr_type[4] = "STRING";
        attr_type[5] = "FLOAT";
        attr_type[6] = "STRING";
        attr_type[7] = "STRING";
        attr_type[8] = "STRING";
        attr_type[9] = "STRING";
        attr_type[10] = "STRING";
        attr_type[11] = "STRING";
        attr_type[12] = "STRING";
        attr_type[13] = "STRING";
        attr_type[14] = "STRING";
        attr_type[15] = "STRING";
        attr_type[16] = "STRING";
        return attr_type;
    }

    @Override
    public void initAcceptedInputFileFormats() {
        this.acceptedInputFileFormats = new HashSet<>();
        this.acceptedInputFileFormats.add(".txt");
    }
    
}
