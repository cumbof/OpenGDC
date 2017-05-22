/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
import opengdc.integration.MIRBase;
import opengdc.util.FSUtils;
import opengdc.util.FormatUtils;
import opengdc.util.GDCQuery;

/**
 *
 * @author fabio
 */
public class MiRNAExpressionQuantificationParser extends BioParser {

    @Override
    public int convert(String program, String disease, String dataType, String inPath, String outPath) {
        int acceptedFiles = FSUtils.acceptedFilesInFolder(inPath, getAcceptedInputFileFormats());
        System.err.println("Data Amount: " + acceptedFiles + " files" + "\n\n");
        GUI.appendLog("Data Amount: " + acceptedFiles + " files" + "\n\n");
        
        if (acceptedFiles == 0)
            return 1;
        
        HashSet<String> filesPathConverted = new HashSet<>();
        
        HashMap<String, HashMap<String, String>> mirnaid2coordinates = MIRBase.getMirnaid2coordinates();
        
        File[] files = (new File(inPath)).listFiles();
        for (File f: files) {
            if (f.isFile()) {
                String extension = FSUtils.getFileExtension(f);
                if (getAcceptedInputFileFormats().contains(extension)) {
                    System.err.println("Processing " + f.getName());
                    GUI.appendLog("Processing " + f.getName() + "\n");
                    
                    String file_uuid = f.getName().split("_")[0];
                    String aliquot_uuid = GDCQuery.retrieveAliquotFromFileUUID(file_uuid);
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
                                    String mirna_id = line_split[0];
                                    String read_count = line_split[1];
                                    String reads_per_million_mirna_mapped = line_split[2];
                                    String cross_mapped = line_split[3];

                                    HashMap<String, String> coordinates = null;
                                    if (mirnaid2coordinates.containsKey(mirna_id)) {
                                        coordinates = mirnaid2coordinates.get(mirna_id);

                                        String chr = coordinates.get("CHR");
                                        String start = coordinates.get("START");
                                        String end = coordinates.get("END");
                                        String strand = coordinates.get("STRAND");
                                        String entrez = "NA";
                                        
                                        // retrieve entrez_id from GeneNames (HUGO)
                                        String entrez_tmp = GeneNames.getEntrezFromMirnaID(mirna_id);
                                        if (entrez_tmp != null)
                                            entrez = entrez_tmp;

                                        ArrayList<String> values = new ArrayList<>();
                                        values.add(chr);
                                        values.add(start);
                                        values.add(end);
                                        values.add(strand);
                                        values.add(mirna_id);
                                        values.add(entrez);
                                        values.add(read_count);
                                        values.add(reads_per_million_mirna_mapped);
                                        values.add(cross_mapped);

                                        Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.createEntry(this.getFormat(), values, getHeader())).getBytes("UTF-8"), StandardOpenOption.APPEND);
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

    @Override
    public String[] getHeader() {
        String[] header = new String[9];
        header[0] = "chr";
        header[1] = "start";
        header[2] = "stop";
        header[3] = "strand";
        header[4] = "mirna_id";
        header[5] = "entrez_id";
        header[6] = "read_count";
        header[7] = "reads_per_million_mirna_mapped";
        header[8] = "cross_mapped";
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
        attr_type[8] = "STRING";
        return attr_type;
    }

    @Override
    public void initAcceptedInputFileFormats() {
        this.acceptedInputFileFormats = new HashSet<>();
        this.acceptedInputFileFormats.add(".txt");
    }
    
}
