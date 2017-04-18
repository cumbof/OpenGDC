/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.parser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import opengdc.GUI;
import opengdc.reader.GeneExpressionQuantificationReader;
import opengdc.util.FSUtils;
import opengdc.util.FormatUtils;

/**
 *
 * @author fabio
 */
public class GeneExpressionQuantificationParser extends BioParser {

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
                // start with 'counts' file and manually retrieve the related 'FPKM' and 'FPKM-UQ' files
                if (getAcceptedInputFileFormats().contains(extension) && extension.toLowerCase().trim().equals("counts")) {
                    // TODO: we need a common name for 'counts', 'FPKM' and 'FPKM-UQ' files for the same aliquot
                    String uuid = f.getName().split("_")[0];
                    // TODO: retrieve the related 'FPKM' and 'FPKM-UQ' files
                    File fpkm_file = new File("");
                    File fpkmuq_file = new File("");
                    
                    System.err.println("Processing " + uuid + " (counts, FPKM, FPKM-UQ)");
                    GUI.appendLog("Processing " + uuid + " (counts, FPKM, FPKM-UQ)" + "\n");

                    HashMap<String, String> ensembl2count = GeneExpressionQuantificationReader.getEnsembl2Value(f);
                    HashMap<String, String> ensembl2fpkm = GeneExpressionQuantificationReader.getEnsembl2Value(fpkm_file);
                    HashMap<String, String> ensembl2fpkmuq = GeneExpressionQuantificationReader.getEnsembl2Value(fpkmuq_file);
                    
                    HashSet<String> ensembls = new HashSet<>();
                    ensembls.addAll(ensembl2count.keySet());
                    ensembls.addAll(ensembl2fpkm.keySet());
                    ensembls.addAll(ensembl2fpkmuq.keySet());
                    
                    if (!ensembls.isEmpty()) {
                        try {
                            Files.write((new File(outPath + uuid + "." + this.getFormat())).toPath(), (FormatUtils.initDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.CREATE);
                            for (String ensembl_id: ensembls) {
                                /** convert ensembl_id to gene symbol and retrieve chromosome, start and end position, and strand **/
                                String chr = "";
                                String start = "";
                                String end = "";
                                String strand = "";
                                String gene_symbol = "";
                                /***************************************************************************************************/
                                String htseq_count = (ensembl2count.containsKey(ensembl_id)) ? ensembl2count.get(ensembl_id) : "NA";
                                String fpkm_uq = (ensembl2fpkmuq.containsKey(ensembl_id)) ? ensembl2fpkmuq.get(ensembl_id) : "NA";
                                String fpkm = (ensembl2fpkm.containsKey(ensembl_id)) ? ensembl2fpkm.get(ensembl_id) : "NA";

                                ArrayList<String> values = new ArrayList<>();
                                values.add(chr);
                                values.add(start);
                                values.add(end);
                                values.add(strand);
                                values.add(ensembl_id);
                                values.add(gene_symbol);
                                values.add(htseq_count);
                                values.add(fpkm_uq);
                                values.add(fpkm);
                                Files.write((new File(outPath + uuid + "." + this.getFormat())).toPath(), (FormatUtils.createEntry(this.getFormat(), values, getHeader())).getBytes("UTF-8"), StandardOpenOption.APPEND);
                            }
                            Files.write((new File(outPath + uuid + "." + this.getFormat())).toPath(), (FormatUtils.endDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.APPEND);
                            filesPathConverted.add(outPath + uuid + "." + this.getFormat());
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
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
        String[] header = new String[9];
        header[0] = "chr";
        header[1] = "start";
        header[2] = "stop";
        header[3] = "strand";
        header[4] = "ensembl_id";
        header[5] = "gene_symbol";
        header[6] = "htseq_count";
        header[7] = "fpkm_uq";
        header[8] = "fpkm";
        return header;
    }

    @Override
    public String[] getAttributesType() {
        String[] attr_type = new String[9];
        attr_type[0] = "STRING";
        attr_type[1] = "LONG";
        attr_type[2] = "LONG";
        attr_type[3] = "CHAR";
        attr_type[4] = "STRING";
        attr_type[5] = "STRING";
        attr_type[6] = "LONG";
        attr_type[7] = "FLOAT";
        attr_type[8] = "FLOAT";
        return attr_type;
    }

    @Override
    public void initAcceptedInputFileFormats() {
        this.acceptedInputFileFormats = new HashSet<>();
        this.acceptedInputFileFormats.add(".txt");
        this.acceptedInputFileFormats.add(".counts");
    }
    
}
