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
import opengdc.util.FSUtils;
import opengdc.util.FormatUtils;
import opengdc.util.GDCQuery;

/**
 *
 * @author fabio
 */
public class CopyNumberSegmentParser extends BioParser {

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
                    String aliquot_id_path = "cases.samples.portions.analytes.aliquots.aliquot_id";
                    attributes.add(aliquot_id_path);
                    HashMap<String, String> file_info = GDCQuery.retrieveExpInfoFromAttribute("files.file_id", file_uuid, attributes, 0, 0, null).get(0);
                    String aliquot_uuid = "";
                    if (file_info != null)
                        if (file_info.containsKey(aliquot_id_path))
                            aliquot_uuid = file_info.get(aliquot_id_path);
                    
                    if (!aliquot_uuid.trim().equals("")) {
                        try {
                            String suffix_id = this.getOpenGDCSuffix(dataType, false);
                            String filePath = outPath + aliquot_uuid + "-" + suffix_id + "." + this.getFormat();
                            Files.write((new File(filePath)).toPath(), (FormatUtils.initDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.CREATE);
                            /** store entries **/
                            HashMap<Integer, HashMap<Integer, ArrayList<ArrayList<String>>>> dataMapChr = new HashMap<>();
                            
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
                                    String chr = line_split[1];
                                    if (!chr.toLowerCase().contains("chr")) chr = "chr"+chr;
                                    String start = line_split[2];
                                    String end = line_split[3];
                                    String strand = "*"; // unknown strand for copy number variation data
                                    String num_probes = line_split[4];
                                    String segment_mean = line_split[5];

                                    ArrayList<String> values = new ArrayList<>();
                                    values.add(parseValue(chr, 0));
                                    values.add(parseValue(start, 1));
                                    values.add(parseValue(end, 2));
                                    values.add(parseValue(strand, 3));
                                    values.add(parseValue(num_probes, 4));
                                    values.add(parseValue(segment_mean, 5));
                                    
                                    /**********************************************************************/
                                    /** populate dataMap then sort genomic coordinates and print entries **/
                                    int chr_id = Integer.parseInt(parseValue(chr, 0).replaceAll("chr", "").replaceAll("X", "23").replaceAll("Y", "24"));
                                    int start_id = Integer.parseInt(parseValue(start, 1));
                                    HashMap<Integer, ArrayList<ArrayList<String>>> dataMapStart = new HashMap<>();
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
                            br.close();
                            in.close();
                            fstream.close();
                            
                            // sort genomic coordinates and print data
                            this.printData((new File(filePath)).toPath(), dataMapChr, this.getFormat(), getHeader());

                            Files.write((new File(filePath)).toPath(), (FormatUtils.endDocument(this.getFormat())).getBytes("UTF-8"), StandardOpenOption.APPEND);
                            filesPathConverted.add(filePath);
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
        String[] header = new String[6];
        header[0] = "chrom";
        header[1] = "start";
        header[2] = "end";
        header[3] = "strand";
        header[4] = "num_probes";
        header[5] = "segment_mean";
        return header;
    }

    @Override
    public String[] getAttributesType() {
        String[] attr_type = new String[6];
        attr_type[0] = "STRING";
        attr_type[1] = "LONG";
        attr_type[2] = "LONG";
        attr_type[3] = "CHAR";
        attr_type[4] = "LONG";
        attr_type[5] = "DOUBLE";
        return attr_type;
    }

    @Override
    public void initAcceptedInputFileFormats() {
        this.acceptedInputFileFormats = new HashSet<>();
        this.acceptedInputFileFormats.add(".txt");
    }
    
}
