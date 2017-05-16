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
import java.util.HashSet;
import opengdc.GUI;
import opengdc.util.FSUtils;
import opengdc.util.FormatUtils;
import opengdc.util.GDCQuery;

/**
 *
 * @author fabio
 */
public class IsoformExpressionQuantificationParser extends BioParser {

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
                                    String isoform_coordinates = line_split[1];
                                    String[] coordinates_split = isoform_coordinates.split(":");
                                    String chr = coordinates_split[1];
                                    String start = coordinates_split[2];
                                    String end = coordinates_split[3];
                                    String strand = coordinates_split[4];

                                    String mirna_id = line_split[0];
                                    String read_count = line_split[2];
                                    String reads_per_million_mirna_mapped = line_split[3];
                                    String cross_mapped = line_split[4];
                                    String mirna_region = line_split[5];

                                    ArrayList<String> values = new ArrayList<>();
                                    values.add(chr);
                                    values.add(start);
                                    values.add(end);
                                    values.add(strand);
                                    values.add(mirna_id);
                                    values.add(read_count);
                                    values.add(reads_per_million_mirna_mapped);
                                    values.add(cross_mapped);
                                    values.add(mirna_region);

                                    Files.write((new File(outPath + aliquot_uuid + "." + this.getFormat())).toPath(), (FormatUtils.createEntry(this.getFormat(), values, getHeader())).getBytes("UTF-8"), StandardOpenOption.APPEND);
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
        header[5] = "read_count";
        header[6] = "reads_per_million_mirna_mapped";
        header[7] = "cross_mapped";
        header[8] = "mirna_region";
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
        attr_type[5] = "LONG";
        attr_type[6] = "FLOAT";
        attr_type[7] = "STRING";
        attr_type[8] = "STRING";
        return attr_type;
    }

    @Override
    public void initAcceptedInputFileFormats() {
        this.acceptedInputFileFormats = new HashSet<>();
        this.acceptedInputFileFormats.add(".txt");
    }
    
}
