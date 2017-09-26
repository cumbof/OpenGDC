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

import opengdc.GUI;
import opengdc.util.FSUtils;
import opengdc.reader.MaskedSomaticMutationReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import opengdc.util.FormatUtils;

/**
 *
 * @author fabio
 */
public class MaskedSomaticMutationParser extends BioParser {

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
                    
                    HashSet<String> aliquot_uuids = MaskedSomaticMutationReader.getUUIDsFromMaf(f.getAbsolutePath());
                    for (String aliquot_uuid: aliquot_uuids) {
                        HashMap<Integer, HashMap<String, String>> uuidData = MaskedSomaticMutationReader.getUUIDDataFromMaf(f.getAbsolutePath(), aliquot_uuid);
                        //System.err.println("data: "+uuidData.size());
                        
                        if (!uuidData.isEmpty()) {
                            try {
                                if (!filesPathConverted.contains(outPath + aliquot_uuid + "." + this.getFormat())) {
                                    Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.initDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.CREATE);
                                    filesPathConverted.add(outPath + aliquot_uuid + "." + this.getFormat());
                                }
                            
                                for (int entry: uuidData.keySet()) {
                                    ArrayList<String> values = new ArrayList<>();
                                    values.add(parseValue(uuidData.get(entry).get("chromosome"), 0));
                                    values.add(parseValue(uuidData.get(entry).get("start_position"), 1));
                                    values.add(parseValue(uuidData.get(entry).get("end_position"), 2));
                                    values.add(parseValue(uuidData.get(entry).get("strand"), 3));
                                    values.add(parseValue(uuidData.get(entry).get("hugo_symbol"), 4));
                                    values.add(parseValue(uuidData.get(entry).get("entrez_gene_id"), 5));
                                    /*values.add(uuidData.get(entry).get("center"));
                                    values.add(uuidData.get(entry).get("ncbi_build"));*/
                                    values.add(parseValue(uuidData.get(entry).get("variant_classification"), 6));
                                    values.add(parseValue(uuidData.get(entry).get("variant_type"), 7));
                                    values.add(parseValue(uuidData.get(entry).get("reference_allele"), 8));
                                    values.add(parseValue(uuidData.get(entry).get("tumor_seq_allele1"), 9));
                                    values.add(parseValue(uuidData.get(entry).get("tumor_seq_allele2"), 10));
                                    values.add(parseValue(uuidData.get(entry).get("dbsnp_rs"), 11));
                                    //values.add(uuidData.get(entry).get("dbsnp_val_status"));
                                    values.add(parseValue(uuidData.get(entry).get("tumor_sample_barcode"), 12));
                                    values.add(parseValue(uuidData.get(entry).get("matched_norm_sample_barcode"), 13));
                                    values.add(parseValue(uuidData.get(entry).get("match_norm_seq_allele1"), 14));
                                    values.add(parseValue(uuidData.get(entry).get("match_norm_seq_allele2"), 15));
                                    /*values.add(uuidData.get(entry).get("tumor_validation_allele1"));
                                    values.add(uuidData.get(entry).get("tumor_validation_allele2"));
                                    values.add(uuidData.get(entry).get("match_norm_validation_allele1"));
                                    values.add(uuidData.get(entry).get("match_norm_validation_allele2"));
                                    values.add(uuidData.get(entry).get("verification_status"));
                                    values.add(uuidData.get(entry).get("validation_status"));
                                    values.add(uuidData.get(entry).get("mutation_status"));
                                    values.add(uuidData.get(entry).get("sequencing_phase"));
                                    values.add(uuidData.get(entry).get("sequence_source"));
                                    values.add(uuidData.get(entry).get("validation_method"));
                                    values.add(uuidData.get(entry).get("score"));
                                    values.add(uuidData.get(entry).get("bam_file"));
                                    values.add(uuidData.get(entry).get("sequencer"));*/
                                    values.add(parseValue(uuidData.get(entry).get("tumor_sample_uuid"), 16));
                                    values.add(parseValue(uuidData.get(entry).get("matched_norm_sample_uuid"), 17));
                                    /*values.add(uuidData.get(entry).get("hgvsc"));
                                    values.add(uuidData.get(entry).get("hgvsp"));
                                    values.add(uuidData.get(entry).get("hgvsp_short"));
                                    values.add(uuidData.get(entry).get("transcript_id"));
                                    values.add(uuidData.get(entry).get("exon_number"));
                                    values.add(uuidData.get(entry).get("t_depth"));
                                    values.add(uuidData.get(entry).get("t_ref_count"));
                                    values.add(uuidData.get(entry).get("t_alt_count"));
                                    values.add(uuidData.get(entry).get("n_depth_count"));
                                    values.add(uuidData.get(entry).get("n_ref_count"));
                                    values.add(uuidData.get(entry).get("n_alt_count"));
                                    values.add(uuidData.get(entry).get("all_effects_count"));
                                    values.add(uuidData.get(entry).get("allele"));
                                    values.add(uuidData.get(entry).get("gene"));
                                    values.add(uuidData.get(entry).get("feature"));
                                    values.add(uuidData.get(entry).get("feature_type"));
                                    values.add(uuidData.get(entry).get("consequence"));
                                    values.add(uuidData.get(entry).get("cdna_position"));
                                    values.add(uuidData.get(entry).get("cds_position"));
                                    values.add(uuidData.get(entry).get("protein_position"));
                                    values.add(uuidData.get(entry).get("amino_acids"));
                                    values.add(uuidData.get(entry).get("codons"));
                                    values.add(uuidData.get(entry).get("existing_variation"));
                                    values.add(uuidData.get(entry).get("allele_num"));
                                    values.add(uuidData.get(entry).get("distance"));
                                    values.add(uuidData.get(entry).get("transcript_strand"));
                                    values.add(uuidData.get(entry).get("symbol"));
                                    values.add(uuidData.get(entry).get("symbol_source"));
                                    values.add(uuidData.get(entry).get("hgnc_id"));
                                    values.add(uuidData.get(entry).get("biotype"));
                                    values.add(uuidData.get(entry).get("canonical"));
                                    values.add(uuidData.get(entry).get("ccds"));
                                    values.add(uuidData.get(entry).get("ensp"));
                                    values.add(uuidData.get(entry).get("swissprot"));
                                    values.add(uuidData.get(entry).get("trembl"));
                                    values.add(uuidData.get(entry).get("uniparc"));
                                    values.add(uuidData.get(entry).get("refseq"));
                                    values.add(uuidData.get(entry).get("sift"));
                                    values.add(uuidData.get(entry).get("polyphen"));
                                    values.add(uuidData.get(entry).get("exon"));
                                    values.add(uuidData.get(entry).get("intron"));
                                    values.add(uuidData.get(entry).get("domains"));
                                    values.add(uuidData.get(entry).get("gmaf"));
                                    values.add(uuidData.get(entry).get("afr_maf"));
                                    values.add(uuidData.get(entry).get("amr_maf"));
                                    values.add(uuidData.get(entry).get("asn_maf"));
                                    values.add(uuidData.get(entry).get("eas_maf"));
                                    values.add(uuidData.get(entry).get("eur_maf"));
                                    values.add(uuidData.get(entry).get("sas_maf"));
                                    values.add(uuidData.get(entry).get("aa_maf"));
                                    values.add(uuidData.get(entry).get("ea_maf"));
                                    values.add(uuidData.get(entry).get("clin_sig"));
                                    values.add(uuidData.get(entry).get("somatic"));
                                    values.add(uuidData.get(entry).get("pubmed"));
                                    values.add(uuidData.get(entry).get("motif_name"));
                                    values.add(uuidData.get(entry).get("motif_pos"));
                                    values.add(uuidData.get(entry).get("high_inf_pos"));
                                    values.add(uuidData.get(entry).get("motif_score_change"));
                                    values.add(uuidData.get(entry).get("impact"));
                                    values.add(uuidData.get(entry).get("pick"));
                                    values.add(uuidData.get(entry).get("variant_class"));
                                    values.add(uuidData.get(entry).get("tsl"));
                                    values.add(uuidData.get(entry).get("hgvs_offset"));
                                    values.add(uuidData.get(entry).get("pheno"));
                                    values.add(uuidData.get(entry).get("minimised"));
                                    values.add(uuidData.get(entry).get("exac_af_af"));
                                    values.add(uuidData.get(entry).get("exac_af_afr"));
                                    values.add(uuidData.get(entry).get("exac_af_amr"));
                                    values.add(uuidData.get(entry).get("exac_af_eas"));
                                    values.add(uuidData.get(entry).get("exac_af_fin"));
                                    values.add(uuidData.get(entry).get("exac_af_nfe"));
                                    values.add(uuidData.get(entry).get("exac_af_oth"));
                                    values.add(uuidData.get(entry).get("exac_af_sas"));
                                    values.add(uuidData.get(entry).get("gene_pheno"));
                                    values.add(uuidData.get(entry).get("filter"));
                                    values.add(uuidData.get(entry).get("src_vcf_id"));
                                    values.add(uuidData.get(entry).get("tumor_bam_uuid"));
                                    values.add(uuidData.get(entry).get("normal_bam_uuid"));
                                    values.add(uuidData.get(entry).get("gdc_validation_status"));
                                    values.add(uuidData.get(entry).get("gdc_valid_somatic"));*/
                                    
                                    Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.createEntry(this.getFormat(), values, getHeader())).getBytes("UTF-8"), StandardOpenOption.APPEND);
                                }
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    
                }
            }
        }
        
        if (!filesPathConverted.isEmpty()) {
            // close documents
            for (String path: filesPathConverted) {
                try {
                    Files.write((new File(path)).toPath(), (FormatUtils.endDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.APPEND);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
        String[] header = new String[18];
        header[0] = "chrom";
        header[1] = "start";
        header[2] = "end";
        header[3] = "strand";
        header[4] = "gene_symbol";
        header[5] = "entrez_gene_id";
        header[6] = "variant_classification";
        header[7] = "variant_type";
        header[8] = "reference_allele";
        header[9] = "tumor_seq_allele1";
        header[10] = "tumor_seq_allele2";
        header[11] = "dbsnp_rs";
        header[12] = "tumor_sample_barcode";
        header[13] = "matched_norm_sample_barcode";
        header[14] = "match_norm_seq_allele1";
        header[15] = "match_norm_seq_allele2";
        header[16] = "tumor_sample_uuid";
        header[17] = "matched_norm_sample_uuid";
        return header;
    }

    @Override
    public String[] getAttributesType() {
        String[] attr_type = new String[18];
        attr_type[0] = "STRING";
        attr_type[1] = "LONG";
        attr_type[2] = "LONG";
        attr_type[3] = "CHAR";
        attr_type[4] = "STRING";
        attr_type[5] = "STRING";
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
        attr_type[17] = "STRING";
        return attr_type;
    }

    @Override
    public void initAcceptedInputFileFormats() {
        this.acceptedInputFileFormats = new HashSet<>();
        this.acceptedInputFileFormats.add(".maf");
    }
    
}
