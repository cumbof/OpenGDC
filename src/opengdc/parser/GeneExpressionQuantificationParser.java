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

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import opengdc.GUI;
import opengdc.integration.Gencode;
import opengdc.integration.GeneNames;
import opengdc.reader.GeneExpressionQuantificationReader;
import opengdc.util.FSUtils;
import opengdc.util.FormatUtils;
import opengdc.util.GDCQuery;

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
        
        // retrive all aliquot IDs
        HashMap<String, String> fileUUID2aliquotUUID = new HashMap<>();
        File[] files = (new File(inPath)).listFiles();
        for (File f: files) {
            if (f.isFile()) {
                String extension = FSUtils.getFileExtension(f);
                if (getAcceptedInputFileFormats().contains(extension)) {
                    String file_uuid = f.getName().split("_")[0];
                    HashSet<String> attributes = new HashSet<>();
                    String aliquot_id_path = "cases.samples.portions.analytes.aliquots.aliquot_id";
                    attributes.add(aliquot_id_path);
                    HashMap<String, String> file_info = GDCQuery.retrieveExpInfoFromAttribute("files.file_id", file_uuid, attributes, 0);
                    String aliquot_uuid = "";
                    if (file_info != null)
                        if (file_info.containsKey(aliquot_id_path))
                            aliquot_uuid = file_info.get(aliquot_id_path);
                    fileUUID2aliquotUUID.put(file_uuid, aliquot_uuid);
                }
            }
        }
        
        for (File f: files) {
            if (f.isFile()) {
                String extension = FSUtils.getFileExtension(f);
                // start with 'counts' file and manually retrieve the related 'FPKM' and 'FPKM-UQ' files
                if (getAcceptedInputFileFormats().contains(extension) && extension.toLowerCase().trim().equals(".counts")) {
                    // TODO: we need a common name for 'counts', 'FPKM' and 'FPKM-UQ' files for the same aliquot
                    String file_uuid = f.getName().split("_")[0];
                    String aliquot_uuid = fileUUID2aliquotUUID.get(file_uuid);
                    if (!aliquot_uuid.trim().equals("")) {
                        // retrieve 'FPKM' and 'FPKM-UQ' files with the same aliquot_uuid (related to the 'counts' file)
                        File fpkm_file = getRelatedFile(files, aliquot_uuid, fileUUID2aliquotUUID, "fpkm.txt");
                        File fpkmuq_file = getRelatedFile(files, aliquot_uuid, fileUUID2aliquotUUID, "fpkm-uq.txt");

                        System.err.println("Processing " + aliquot_uuid + " (counts, FPKM, FPKM-UQ)");
                        GUI.appendLog("Processing " + aliquot_uuid + " (counts, FPKM, FPKM-UQ)" + "\n");

                        HashMap<String, String> ensembl2count = GeneExpressionQuantificationReader.getEnsembl2Value(f);
                        HashMap<String, String> ensembl2fpkm = GeneExpressionQuantificationReader.getEnsembl2Value(fpkm_file);
                        HashMap<String, String> ensembl2fpkmuq = GeneExpressionQuantificationReader.getEnsembl2Value(fpkmuq_file);

                        HashSet<String> ensembls = new HashSet<>();
                        ensembls.addAll(ensembl2count.keySet());
                        ensembls.addAll(ensembl2fpkm.keySet());
                        ensembls.addAll(ensembl2fpkmuq.keySet());

                        if (!ensembls.isEmpty()) {
                            try {
                                Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.initDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.CREATE);
                                /** store entries **/
                                HashMap<String, HashMap<String, ArrayList<ArrayList<String>>>> dataMapChr = new HashMap<>();
                                
                                for (String ensembl_id: ensembls) {
                                    /** convert ensembl_id to symbol and retrieve chromosome, start and end position, strand, and other relevant info **/
                                    // remove ensembl version from id
                                    String ensembl_id_noversion = ensembl_id.split("\\.")[0];
                                    ArrayList<HashMap<String, String>> gencode_data = Gencode.extractGencodeInfo("ensembl_id", ensembl_id_noversion, "gene");
                                    if (!gencode_data.isEmpty()) {
                                        // get first entry
                                        HashMap<String, String> gene_info = gencode_data.get(0);

                                        String chr = gene_info.get("CHR");
                                        if (!chr.toLowerCase().contains("chr")) chr = "chr"+chr;
                                        String start = gene_info.get("START");
                                        String end = gene_info.get("END");
                                        String strand = gene_info.get("STRAND");
                                        String gene_symbol = gene_info.get("SYMBOL");
                                        String type = gene_info.get("TYPE");

                                        // trying to retrive the entrez_id starting with the symbol from GeneNames (HUGO)
                                        String entrez = "NA";
                                        String entrez_tmp = GeneNames.getEntrezFromSymbol(gene_symbol);
                                        if (entrez_tmp != null)
                                            entrez = entrez_tmp;
                                        else
                                            entrez = GeneNames.getEntrezFromEnsemblID(ensembl_id_noversion);
                                        
                                        /***************************************************************************************************/
                                        String htseq_count = (ensembl2count.containsKey(ensembl_id)) ? ensembl2count.get(ensembl_id) : "NA";
                                        String fpkm_uq = (ensembl2fpkmuq.containsKey(ensembl_id)) ? ensembl2fpkmuq.get(ensembl_id) : "NA";
                                        String fpkm = (ensembl2fpkm.containsKey(ensembl_id)) ? ensembl2fpkm.get(ensembl_id) : "NA";

                                        ArrayList<String> values = new ArrayList<>();
                                        values.add(parseValue(chr, 0));
                                        values.add(parseValue(start, 1));
                                        values.add(parseValue(end, 2));
                                        values.add(parseValue(strand, 3));
                                        values.add(parseValue(ensembl_id, 4));
                                        values.add(parseValue(entrez, 5));
                                        values.add(parseValue(gene_symbol, 6));
                                        values.add(parseValue(type, 7));
                                        values.add(parseValue(htseq_count, 8));
                                        values.add(parseValue(fpkm_uq, 9));
                                        values.add(parseValue(fpkm, 10));
                                        
                                        /**********************************************************************/
                                        /** populate dataMap then sort genomic coordinates and print entries **/
                                        String chr_id = parseValue(chr, 0).replaceAll("chr", "");
                                        String start_id = parseValue(start, 1);
                                        HashMap<String, ArrayList<ArrayList<String>>> dataMapStart = new HashMap<>();
                                        ArrayList<ArrayList<String>> dataList = new ArrayList<>();
                                        if (dataMapChr.containsKey(chr_id)) {
                                            dataMapStart = dataMapChr.get(chr_id);                                        
                                            if (dataMapStart.containsKey(start_id))
                                                dataList = dataMapStart.get(start_id);
                                            dataList.add(values);
                                        }
                                        else
                                            dataList.add(values);
                                        dataMapStart.put(start_id, dataList);
                                        dataMapChr.put(chr_id, dataMapStart);
                                        /**********************************************************************/
                                        
                                        // decomment this line to print entries without sorting genomic coordinates
                                        //Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.createEntry(this.getFormat(), values, getHeader())).getBytes("UTF-8"), StandardOpenOption.APPEND);
                                    }
                                }
                                // sort genomic coordinates and print data
                                this.printData((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), dataMapChr, this.getFormat(), getHeader());
                                
                                Files.write((new File(outPath +  aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.endDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.APPEND);
                                filesPathConverted.add(outPath + file_uuid + "." + this.getFormat());
                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
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
        String[] header = new String[11];
        header[0] = "chrom";
        header[1] = "start";
        header[2] = "end";
        header[3] = "strand";
        header[4] = "ensembl_gene_id";
        header[5] = "entrez_gene_id";
        header[6] = "gene_symbol";
        header[7] = "type";
        header[8] = "htseq_count";
        header[9] = "fpkm_uq";
        header[10] = "fpkm";
        return header;
    }

    @Override
    public String[] getAttributesType() {
        String[] attr_type = new String[11];
        attr_type[0] = "STRING";
        attr_type[1] = "LONG";
        attr_type[2] = "LONG";
        attr_type[3] = "CHAR";
        attr_type[4] = "STRING";
        attr_type[5] = "STRING";
        attr_type[6] = "STRING";
        attr_type[7] = "STRING";
        attr_type[8] = "LONG";
        attr_type[9] = "DOUBLE";
        attr_type[10] = "DOUBLE";
        return attr_type;
    }

    @Override
    public void initAcceptedInputFileFormats() {
        this.acceptedInputFileFormats = new HashSet<>();
        this.acceptedInputFileFormats.add(".txt");
        this.acceptedInputFileFormats.add(".counts");
    }

    private File getRelatedFile(File[] files, String reference_aliquot_uuid, HashMap<String, String> fileUUID2aliquotUUID, String suffix) {
        for (File f: files) {
            if (f.isFile()) {
                if (f.getName().toLowerCase().trim().endsWith(suffix)) {
                    String file_uuid = f.getName().split("_")[0];
                    String aliquot_uuid = fileUUID2aliquotUUID.get(file_uuid);
                    
                    if (aliquot_uuid.equals(reference_aliquot_uuid))
                        return f;
                }
            }
        }
        return null;
    }
    
}
