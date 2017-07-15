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
import opengdc.GUI;
import opengdc.integration.GeneNames;
import opengdc.integration.NCBI;
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
                                            if (!gene_symbols_comp.equals(".")) {
                                                HashMap<String, String> ncbi_data = NCBI.extractNCBIinfo(chr, gene_symbols_comp, start,end , gene_types_comp, transcript_ids_comp, positions_to_tss_comp);
                                                strand = ncbi_data.get("STRAND");
                                                gene_symbol = ncbi_data.get("SYMBOL");
                                                gene_type = ncbi_data.get("GENE_TYPE");
                                                transcript_id = ncbi_data.get("TRANSCRIPT_ID");
                                                position_to_tss = ncbi_data.get("POSITION_TO_TSS");			
                                                entrez_id = ncbi_data.get("ENTREZ");
                                                all_entrez_ids = ncbi_data.get("ENTREZ_IDs");
                                                all_gene_symbols = ncbi_data.get("GENE_SYMBOLS");
                                                all_gene_types = ncbi_data.get("GENE_TYPES");
                                                all_transcript_ids = ncbi_data.get("TRANSCRIPT_IDS");
                                                all_positions_to_tss = ncbi_data.get("POSITIONS_TO_TSS");
                                            }

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
                                    
                                    
                                    /*************************************************************************************************************/
                                    /************************************************ OLD METHOD: ************************************************/
                                    /*********************************************** QUERYING NCBI ***********************************************/
                                    /*************************************************************************************************************/
                                    
                                    
                                    /*String[] line_split = line.split("\t");
                                    String chr = line_split[2];
                                    if (!chr.toLowerCase().contains("chr")) chr = "chr"+chr;
                                    String start = line_split[3];
                                    String end = line_split[4];
                                    String strand = "*";
                                    String composite_element_ref = line_split[0];
                                    String beta_value = line_split[1];
                                    //String gene_symbol = line_split[5].split(";")[0];
	                            String gene_symbol = line_split[5];
                                    String entrez = "NA";
                                    String gene_type = line_split[6].split(";")[0];
                                    String transcript_id = line_split[7];
                                    String position_to_tss = line_split[8];
                                    String cgi_coordinate = line_split[9];
                                    String feature_type = line_split[10];
                                    
                                    // skip non-valid entry
                                    if (!chr.equals("*")) {
                                        // trying to retrive the entrez_id starting with the gene_symbol from GeneNames (HUGO)
                                        String[] gene_symbol_split = gene_symbol.split(";");
                                        for (String gene_sym: gene_symbol_split) {
                                            String entrez_tmp = GeneNames.getEntrezFromSymbol(gene_sym);
                                            if (entrez_tmp != null) {
                                                entrez = entrez_tmp;
                                                // trying to retrieve the strand starting with the entrez from NCBI
                                                HashMap<String, String> entrez_data = NCBI.getGeneInfo(entrez, gene_symbol);
                                                if (!entrez_data.isEmpty()) {
                                                    String strand_tmp = entrez_data.get("STRAND");
                                                    if (!strand_tmp.trim().equals("") && !strand_tmp.trim().toLowerCase().equals("na") && !strand_tmp.trim().toLowerCase().equals("null"))
                                                        strand = strand_tmp;
                                                }
                                            }
                                        }

                                        ArrayList<String> values = new ArrayList<>();
                                        values.add(parseValue(chr, 0));
                                        values.add(parseValue(start, 1));
                                        values.add(parseValue(end, 2));
                                        values.add(parseValue(strand, 3));
                                        values.add(parseValue(composite_element_ref, 4));
                                        values.add(parseValue(beta_value, 5));
                                        values.add(parseValue(gene_symbol, 6));
                                        values.add(parseValue(entrez, 7));
                                        values.add(parseValue(gene_type, 8));
                                        values.add(parseValue(transcript_id, 9));
                                        values.add(parseValue(position_to_tss, 10));
                                        values.add(parseValue(cgi_coordinate, 11));
                                        values.add(parseValue(feature_type, 12));

                                        Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.createEntry(this.getFormat(), values, getHeader())).getBytes("UTF-8"), StandardOpenOption.APPEND);
                                    }*/
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
