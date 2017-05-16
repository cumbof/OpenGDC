/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
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
    /*private static final int CENTER_INDEX = 2;
    private static final int NCBI_BUILD_INDEX = 3;*/
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
    //private static final int DBSNP_VAL_STATUS_INDEX = 14;
    private static final int TUMOR_SAMPLE_BARCODE_INDEX = 15;
    private static final int MATCHED_NORM_SAMPLE_BARCODE_INDEX = 16;
    private static final int MATCH_NORM_SEQ_ALLELE1_INDEX = 17;
    private static final int MATCH_NORM_SEQ_ALLELE2_INDEX = 18;
    /*private static final int TUMOR_VALIDATION_ALLELE1_INDEX = 19;
    private static final int TUMOR_VALIDATION_ALLELE2_INDEX = 20;
    private static final int MATCH_NORM_VALIDATION_ALLELE1_INDEX = 21;
    private static final int MATCH_NORM_VALIDATION_ALLELE2_INDEX = 22;
    private static final int VERIFICATION_STATUS_INDEX = 23;
    private static final int VALIDATION_STATUS_INDEX = 24;
    private static final int MUTATION_STATUS_INDEX = 25;
    private static final int SEQUENCING_PHASE_INDEX = 26;
    private static final int SEQUENCE_SOURCE_INDEX = 27;
    private static final int VALIDATION_METHOD_INDEX = 28;
    private static final int SCORE_INDEX = 29;
    private static final int BAM_FILE_INDEX = 30;
    private static final int SEQUENCER_INDEX = 31;*/
    private static final int TUMOR_SAMPLE_UUID_INDEX = 32;
    private static final int MATCHED_NORM_SAMPLE_UUID_INDEX = 33;
    /*private static final int HGVSC_INDEX = 34;
    private static final int HGVSP_INDEX = 35;
    private static final int HGVSP_SHORT_INDEX = 36;
    private static final int TRANSCRIPT_ID_INDEX = 37;
    private static final int EXON_NUMBER_INDEX = 38;
    private static final int T_DEPTH_INDEX = 39;
    private static final int T_REF_COUNT_INDEX = 40;
    private static final int T_ALT_COUNT_INDEX = 41;
    private static final int N_DEPTH_COUNT_INDEX = 42;
    private static final int N_REF_COUNT_INDEX = 43;
    private static final int N_ALT_COUNT_INDEX = 44;
    private static final int ALL_EFFECTS_COUNT_INDEX = 45;
    private static final int ALLELE_INDEX = 46;
    private static final int GENE_INDEX = 47;
    private static final int FEATURE_INDEX = 48;
    private static final int FEATURE_TYPE_INDEX = 49;
    private static final int CONSEQUENCE_INDEX = 50;
    private static final int CDNA_POSITION_INDEX = 51;
    private static final int CDS_POSITION_INDEX = 52;
    private static final int PROTEIN_POSITION_INDEX = 53;
    private static final int AMINO_ACIDS_INDEX = 54;
    private static final int CODONS_INDEX = 55;
    private static final int EXISTING_VARIATION_INDEX = 56;
    private static final int ALLELE_NUM_INDEX = 57;
    private static final int DISTANCE_INDEX = 58;
    private static final int TRANSCRIPT_STRAND_INDEX = 59;
    private static final int SYMBOL_INDEX = 60;
    private static final int SYMBOL_SOURCE_INDEX = 61;
    private static final int HGNC_ID_INDEX = 62;
    private static final int BIOTYPE_INDEX = 63;
    private static final int CANONICAL_INDEX = 64;
    private static final int CCDS_INDEX = 65;
    private static final int ENSP_INDEX = 66;
    private static final int SWISSPROT_INDEX = 67;
    private static final int TREMBL_INDEX = 68;
    private static final int UNIPARC_INDEX = 69;
    private static final int REFSEQ_INDEX = 70;
    private static final int SIFT_INDEX = 71;
    private static final int POLYPHEN_INDEX = 72;
    private static final int EXON_INDEX = 73;
    private static final int INTRON_INDEX = 74;
    private static final int DOMAINS_INDEX = 75;
    private static final int GMAF_INDEX = 76;
    private static final int AFR_MAF_INDEX = 77;
    private static final int AMR_MAF_INDEX = 78;
    private static final int ASN_MAF_INDEX = 79;
    private static final int EAS_MAF_INDEX = 80;
    private static final int EUR_MAF_INDEX = 81;
    private static final int SAS_MAF_INDEX = 82;
    private static final int AA_MAF_INDEX = 83;
    private static final int EA_MAF_INDEX = 84;
    private static final int CLIN_SIG_INDEX = 85;
    private static final int SOMATIC_INDEX = 86;
    private static final int PUBMED_INDEX = 87;
    private static final int MOTIF_NAME_INDEX = 88;
    private static final int MOTIF_POS_INDEX = 89;
    private static final int HIGH_INF_POS_INDEX = 90;
    private static final int MOTIF_SCORE_CHANGE_INDEX = 91;
    private static final int IMPACT_INDEX = 92;
    private static final int PICK_INDEX = 93;
    private static final int VARIANT_CLASS_INDEX = 94;
    private static final int TSL_INDEX = 95;
    private static final int HGVS_OFFSET_INDEX = 96;
    private static final int PHENO_INDEX = 97;
    private static final int MINIMISED_INDEX = 98;
    private static final int EXAC_AF_INDEX = 99;
    private static final int EXAC_AF_AFR_INDEX = 100;
    private static final int EXAC_AF_AMR_INDEX = 101;
    private static final int EXAC_AF_EAS_INDEX = 102;
    private static final int EXAC_AF_FIN_INDEX = 103;
    private static final int EXAC_AF_NFE_INDEX = 104;
    private static final int EXAC_AF_OTH_INDEX = 105;
    private static final int EXAC_AF_SAS_INDEX = 106;
    private static final int GENE_PHENO_INDEX = 107;
    private static final int FILTER_INDEX = 108;
    private static final int SRC_VCF_ID_INDEX = 109;
    private static final int TUMOR_BAM_UUID_INDEX = 110;
    private static final int NORMAL_BAM_UUID_INDEX = 111;
    private static final int GDC_VALIDATION_STATUS_INDEX = 112;
    private static final int GDC_VALID_SOMATIC_INDEX = 113;*/

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
                                /*entryData.put("center", line_split[CENTER_INDEX]);
                                entryData.put("ncbi_build", line_split[NCBI_BUILD_INDEX]);*/
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
                                //entryData.put("dbsnp_val_status", line_split[DBSNP_VAL_STATUS_INDEX]);
                                entryData.put("tumor_sample_barcode", line_split[TUMOR_SAMPLE_BARCODE_INDEX]);
                                entryData.put("matched_norm_sample_barcode", line_split[MATCHED_NORM_SAMPLE_BARCODE_INDEX]);
                                entryData.put("match_norm_seq_allele1", line_split[MATCH_NORM_SEQ_ALLELE1_INDEX]);
                                entryData.put("match_norm_seq_allele2", line_split[MATCH_NORM_SEQ_ALLELE2_INDEX]);
                                /*entryData.put("tumor_validation_allele1", line_split[TUMOR_VALIDATION_ALLELE1_INDEX]);
                                entryData.put("tumor_validation_allele2", line_split[TUMOR_VALIDATION_ALLELE2_INDEX]);
                                entryData.put("match_norm_validation_allele1", line_split[MATCH_NORM_VALIDATION_ALLELE1_INDEX]);
                                entryData.put("match_norm_validation_allele2", line_split[MATCH_NORM_VALIDATION_ALLELE2_INDEX]);
                                entryData.put("verification_status", line_split[VERIFICATION_STATUS_INDEX]);
                                entryData.put("validation_status", line_split[VALIDATION_STATUS_INDEX]);
                                entryData.put("mutation_status", line_split[MUTATION_STATUS_INDEX]);
                                entryData.put("sequencing_phase", line_split[SEQUENCING_PHASE_INDEX]);
                                entryData.put("sequence_source", line_split[SEQUENCE_SOURCE_INDEX]);
                                entryData.put("validation_method", line_split[VALIDATION_METHOD_INDEX]);
                                entryData.put("score", line_split[SCORE_INDEX]);
                                entryData.put("bam_file", line_split[BAM_FILE_INDEX]);
                                entryData.put("sequencer", line_split[SEQUENCER_INDEX]);*/
                                entryData.put("tumor_sample_uuid", line_split[TUMOR_SAMPLE_UUID_INDEX]);
                                entryData.put("matched_norm_sample_uuid", line_split[MATCHED_NORM_SAMPLE_UUID_INDEX]);
                                /*entryData.put("hgvsc", line_split[HGVSC_INDEX]);
                                entryData.put("hgvsp", line_split[HGVSP_INDEX]);
                                entryData.put("hgvsp_short", line_split[HGVSP_SHORT_INDEX]);
                                entryData.put("transcript_id", line_split[TRANSCRIPT_ID_INDEX]);
                                entryData.put("exon_number", line_split[EXON_NUMBER_INDEX]);
                                entryData.put("t_depth", line_split[T_DEPTH_INDEX]);
                                entryData.put("t_ref_count", line_split[T_REF_COUNT_INDEX]);
                                entryData.put("t_alt_count", line_split[T_ALT_COUNT_INDEX]);
                                entryData.put("n_depth_count", line_split[N_DEPTH_COUNT_INDEX]);
                                entryData.put("n_ref_count", line_split[N_REF_COUNT_INDEX]);
                                entryData.put("n_alt_count", line_split[N_ALT_COUNT_INDEX]);
                                entryData.put("all_effects_count", line_split[ALL_EFFECTS_COUNT_INDEX]);
                                entryData.put("allele", line_split[ALLELE_INDEX]);
                                entryData.put("gene", line_split[GENE_INDEX]);
                                entryData.put("feature", line_split[FEATURE_INDEX]);
                                entryData.put("feature_type", line_split[FEATURE_TYPE_INDEX]);
                                entryData.put("consequence", line_split[CONSEQUENCE_INDEX]);
                                entryData.put("cdna_position", line_split[CDNA_POSITION_INDEX]);
                                entryData.put("cds_position", line_split[CDS_POSITION_INDEX]);
                                entryData.put("protein_position", line_split[PROTEIN_POSITION_INDEX]);
                                entryData.put("amino_acids", line_split[AMINO_ACIDS_INDEX]);
                                entryData.put("codons", line_split[CODONS_INDEX]);
                                entryData.put("existing_variation", line_split[EXISTING_VARIATION_INDEX]);
                                entryData.put("allele_num", line_split[ALLELE_NUM_INDEX]);
                                entryData.put("distance", line_split[DISTANCE_INDEX]);
                                entryData.put("transcript_strand", line_split[TRANSCRIPT_STRAND_INDEX]);
                                entryData.put("symbol", line_split[SYMBOL_INDEX]);
                                entryData.put("symbol_source", line_split[SYMBOL_SOURCE_INDEX]);
                                entryData.put("hgnc_id", line_split[HGNC_ID_INDEX]);
                                entryData.put("biotype", line_split[BIOTYPE_INDEX]);
                                entryData.put("canonical", line_split[CANONICAL_INDEX]);
                                entryData.put("ccds", line_split[CCDS_INDEX]);
                                entryData.put("ensp", line_split[ENSP_INDEX]);
                                entryData.put("swissprot", line_split[SWISSPROT_INDEX]);
                                entryData.put("trembl", line_split[TREMBL_INDEX]);
                                entryData.put("uniparc", line_split[UNIPARC_INDEX]);
                                entryData.put("refseq", line_split[REFSEQ_INDEX]);
                                entryData.put("sift", line_split[SIFT_INDEX]);
                                entryData.put("polyphen", line_split[POLYPHEN_INDEX]);
                                entryData.put("exon", line_split[EXON_INDEX]);
                                entryData.put("intron", line_split[INTRON_INDEX]);
                                entryData.put("domains", line_split[DOMAINS_INDEX]);
                                entryData.put("gmaf", line_split[GMAF_INDEX]);
                                entryData.put("afr_maf", line_split[AFR_MAF_INDEX]);
                                entryData.put("amr_maf", line_split[AMR_MAF_INDEX]);
                                entryData.put("asn_maf", line_split[ASN_MAF_INDEX]);
                                entryData.put("eas_maf", line_split[EAS_MAF_INDEX]);
                                entryData.put("eur_maf", line_split[EUR_MAF_INDEX]);
                                entryData.put("sas_maf", line_split[SAS_MAF_INDEX]);
                                entryData.put("aa_maf", line_split[AA_MAF_INDEX]);
                                entryData.put("ea_maf", line_split[EA_MAF_INDEX]);
                                entryData.put("clin_sig", line_split[CLIN_SIG_INDEX]);
                                entryData.put("somatic", line_split[SOMATIC_INDEX]);
                                entryData.put("pubmed", line_split[PUBMED_INDEX]);
                                entryData.put("motif_name", line_split[MOTIF_NAME_INDEX]);
                                entryData.put("motif_pos", line_split[MOTIF_POS_INDEX]);
                                entryData.put("high_inf_pos", line_split[HIGH_INF_POS_INDEX]);
                                entryData.put("motif_score_change", line_split[MOTIF_SCORE_CHANGE_INDEX]);
                                entryData.put("impact", line_split[IMPACT_INDEX]);
                                entryData.put("pick", line_split[PICK_INDEX]);
                                entryData.put("variant_class", line_split[VARIANT_CLASS_INDEX]);
                                entryData.put("tsl", line_split[TSL_INDEX]);
                                entryData.put("hgvs_offset", line_split[HGVS_OFFSET_INDEX]);
                                entryData.put("pheno", line_split[PHENO_INDEX]);
                                entryData.put("minimised", line_split[MINIMISED_INDEX]);
                                entryData.put("exac_af_af", line_split[EXAC_AF_INDEX]);
                                entryData.put("exac_af_afr", line_split[EXAC_AF_AFR_INDEX]);
                                entryData.put("exac_af_amr", line_split[EXAC_AF_AMR_INDEX]);
                                entryData.put("exac_af_eas", line_split[EXAC_AF_EAS_INDEX]);
                                entryData.put("exac_af_fin", line_split[EXAC_AF_FIN_INDEX]);
                                entryData.put("exac_af_nfe", line_split[EXAC_AF_NFE_INDEX]);
                                entryData.put("exac_af_oth", line_split[EXAC_AF_OTH_INDEX]);
                                entryData.put("exac_af_sas", line_split[EXAC_AF_SAS_INDEX]);
                                entryData.put("gene_pheno", line_split[GENE_PHENO_INDEX]);
                                entryData.put("filter", line_split[FILTER_INDEX]);
                                entryData.put("src_vcf_id", line_split[SRC_VCF_ID_INDEX]);
                                entryData.put("tumor_bam_uuid", line_split[TUMOR_BAM_UUID_INDEX]);
                                entryData.put("normal_bam_uuid", line_split[NORMAL_BAM_UUID_INDEX]);
                                entryData.put("gdc_validation_status", line_split[GDC_VALIDATION_STATUS_INDEX]);
                                entryData.put("gdc_valid_somatic", line_split[GDC_VALID_SOMATIC_INDEX]);*/
                                
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
