/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.reader;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class MaskedSomaticMutationReader {
    
    private static final int HUGO_SYMBOL_INDEX = 0;
    private static final int ENTREZ_GENE_ID_INDEX = 1;
    private static final int CHROMOSOME_INDEX = 4;
    private static final int START_POSITION_INDEX = 5;
    private static final int END_POSITION_INDEX = 6;
    private static final int STRAND_INDEX = 7;
    private static final int VARIANT_CLASSIFICATION_INDEX = 8;
    private static final int VARIANT_TYPE_INDEX = 9;
    private static final int REFERENCE_ALLELE_INDEX = 10;
    private static final int TUMOR_SEQ_ALLELE1_INDEX = 11;
    private static final int TUMOR_SEQ_ALLELE2_INDEX = 12;
    private static final int DBSNP_RS_INDEX = 13;
    private static final int TUMOR_SAMPLE_BARCODE_INDEX = 15;
    private static final int MATCHED_NORM_SAMPLE_BARCODE_INDEX = 16;
    private static final int MATCH_NORM_SEQ_ALLELE1_INDEX = 17;
    private static final int MATCH_NORM_SEQ_ALLELE2_INDEX = 18;
    private static final int TUMOR_SAMPLE_UUID_INDEX = 32;
    private static final int MATCHED_NORM_SAMPLE_UUID_INDEX = 33;
    
    public static HashSet<String> getUUIDsFromMaf(String mafFilePath) {
        HashSet<String> uuids = new HashSet<>();
        
        try {
            InputStream fstream = new FileInputStream(mafFilePath);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            boolean startReading = false;
            while ((line = br.readLine()) != null) {
                if (!line.trim().equals("")) {
                    String[] line_split = line.split("\t");
                    if (startReading) {
                        uuids.add(line_split[TUMOR_SAMPLE_UUID_INDEX]);
                    }
                    if (line_split[0].toLowerCase().trim().equals("hugo_symbol") && !startReading) {
                        startReading = true;
                    }
                }
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return uuids;
    }
    
    public static HashMap<Integer, HashMap<String, String>> getUUIDDataFromMaf(String mafFilePath, String uuid) {
        HashMap<Integer, HashMap<String, String>> uuidData = new HashMap<>();
        
        try {
            InputStream fstream = new FileInputStream(mafFilePath);
            DataInputStream in = new DataInputStream(fstream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String line;
            boolean startReading = false;
            int record_count = 0;
            while ((line = br.readLine()) != null) {
                try {
                    if (!line.trim().equals("")) {
                        String[] line_split = line.split("\t");
                        if (startReading) {
                            if (line_split[TUMOR_SAMPLE_UUID_INDEX].equals(uuid)) {
                                HashMap<String, String> entryData = new HashMap<>();
                                entryData.put("hugo_symbol", line_split[HUGO_SYMBOL_INDEX]);
                                entryData.put("entrez_gene_id", line_split[ENTREZ_GENE_ID_INDEX]);
                                entryData.put("chromosome", line_split[CHROMOSOME_INDEX]);
                                entryData.put("start_position", line_split[START_POSITION_INDEX]);
                                entryData.put("end_position", line_split[END_POSITION_INDEX]);
                                entryData.put("strand", line_split[STRAND_INDEX]);
                                entryData.put("variant_classification", line_split[VARIANT_CLASSIFICATION_INDEX]);
                                entryData.put("variant_type", line_split[VARIANT_TYPE_INDEX]);
                                entryData.put("reference_allele", line_split[REFERENCE_ALLELE_INDEX]);
                                entryData.put("tumor_seq_allele1", line_split[TUMOR_SEQ_ALLELE1_INDEX]);
                                entryData.put("tumor_seq_allele2", line_split[TUMOR_SEQ_ALLELE2_INDEX]);
                                entryData.put("dbsnp_rs", line_split[DBSNP_RS_INDEX]);
                                entryData.put("tumor_sample_barcode", line_split[TUMOR_SAMPLE_BARCODE_INDEX]);
                                entryData.put("matched_norm_sample_barcode", line_split[MATCHED_NORM_SAMPLE_BARCODE_INDEX]);
                                entryData.put("match_norm_seq_allele1", line_split[MATCH_NORM_SEQ_ALLELE1_INDEX]);
                                entryData.put("match_norm_seq_allele2", line_split[MATCH_NORM_SEQ_ALLELE2_INDEX]);
                                entryData.put("tumor_sample_uuid", line_split[TUMOR_SAMPLE_UUID_INDEX]);
                                entryData.put("matched_norm_sample_uuid", line_split[MATCHED_NORM_SAMPLE_UUID_INDEX]);

                                uuidData.put(record_count, entryData);
                                record_count++;
                            }
                        }
                        if (line_split[TUMOR_SAMPLE_UUID_INDEX].toLowerCase().trim().equals("tumor_sample_uuid"))
                            startReading = true;
                    }
                }
                catch (ArrayIndexOutOfBoundsException ex) {}
            }
            br.close();
            in.close();
            fstream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return uuidData;
    }
    
}
