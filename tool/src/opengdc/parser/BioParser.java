package opengdc.parser;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import javax.swing.JTextPane;
import javax.xml.bind.DatatypeConverter;
import opengdc.GUI;
import opengdc.util.FormatUtils;

/**
 *
 * @author fabio
 */
public abstract class BioParser {
    
    public HashSet<String> acceptedInputFileFormats = new HashSet<>();
    
    public JTextPane logPane = null;
    public void setLogger(JTextPane pane) {
        logPane = pane;
    }
    public JTextPane getLogger() {
        return logPane;
    }
    
    private String defaultFormat = "bed";
    public String getFormat() {
        return defaultFormat;
    }
    public void setFormat(String format) {
        defaultFormat = format;
    }
    
    /*
    * convert codes:
    * 0 -> conversion successfully executed
    * 1 -> no files in inPath
    */
    public abstract int convert(String program, String disease, String dataType, String inPath, String outPath);
    
    public abstract String[] getHeader();
    
    public abstract String[] getAttributesType();
    
    public abstract void initAcceptedInputFileFormats();
    
    public HashSet<String> getAcceptedInputFileFormats() {
        if (acceptedInputFileFormats.isEmpty()) initAcceptedInputFileFormats();
        return this.acceptedInputFileFormats;
    }
    
    public String parseValue(String value, int index) {
        if (value == null)
            value = "";
        if ((value.equals("")) || (value.toLowerCase().equals("na")) || (value.toLowerCase().equals("nan")) || (value.toLowerCase().equals("null")) || (value.toLowerCase().equals("."))) {
            try {
                String attributeType = getAttributesType()[index];
                if (attributeType.toLowerCase().equals("string") || attributeType.toLowerCase().equals("char"))
                    value = "";
                else if (attributeType.toLowerCase().equals("long") || attributeType.toLowerCase().equals("float") || attributeType.toLowerCase().equals("double"))
                    value = "null";
                else
                    value = "";
            }
            catch (Exception e) {}
        }
        return value;
    }
    
    public void printData(Path outFilePath, HashMap<Integer, HashMap<Integer, ArrayList<ArrayList<String>>>> dataMap, String format, String[] header) {
        if (!dataMap.isEmpty()) {
            boolean firstRow = true;
            ArrayList<Integer> chrs = new ArrayList<>(dataMap.keySet());
            // sort by chr
            Collections.sort(chrs);
            for (Integer chr: chrs) {
                HashMap<Integer, ArrayList<ArrayList<String>>> dataList = dataMap.get(chr);
                // sort by start position
                ArrayList<Integer> starts = new ArrayList<>(dataList.keySet());
                Collections.sort(starts);
                for (Integer start: starts) {
                    ArrayList<ArrayList<String>> dataArray = dataList.get(start);
                    for (ArrayList<String> data: dataArray) {
                        try {
                            Files.write(outFilePath, (FormatUtils.createEntry(format, data, header, firstRow)).getBytes("UTF-8"), StandardOpenOption.APPEND);
                            firstRow = false;
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
    public String getOpenGDCSuffix(String dataType, boolean hash) {
        if (!hash) {
            String suffix_id = "";
            if (dataType.equals("Aggregated Somatic Mutation")) 
                suffix_id = "agsm";
            else if (dataType.equals("Annotated Somatic Mutation")) 
                suffix_id = "ansm";
            else if (dataType.trim().equals(""))
                suffix_id = "xxx";
            else {
                String[] dataType_split = dataType.trim().toLowerCase().split(" ");
                for (String w: dataType_split)
                    suffix_id += w.substring(0, 1);
            }
            return suffix_id;
        }
        else {
            try {         
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(dataType.getBytes());
                byte[] digest = md.digest();
                String suffix_id = DatatypeConverter.printHexBinary(digest).toLowerCase();
                // make the hash string shorter (first 12 chars)
                return suffix_id.substring(0, 12);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static String getTissueStatus(String tissue_id) {
        try {
            int tissue_id_int = Integer.parseInt(tissue_id);
            if ((tissue_id_int>0 && tissue_id_int<10) || tissue_id_int==40)
                return "tumoral";
            else if ( tissue_id_int>9 && tissue_id_int<15)
                return "normal";
            else if (tissue_id_int == 20)
                return "control";
            else
                return "undefined";
        } catch (Exception e) {
            return "undefined";
        }
    }
    
    public String checkForNAs(String metaValue) {
        if (metaValue.trim().toLowerCase().equals("na") || metaValue.trim().toLowerCase().equals("null"))
            return "";
        else return metaValue;
    }
    
    public void printErrorFileLog(HashMap<String, String> error_inputFile2outputFile){
        for (String f: error_inputFile2outputFile.keySet())   {
            File outputFile = new File(error_inputFile2outputFile.get(f));
            if (outputFile.exists()) {
                if (outputFile.length() == 0) { // return length in bytes
                    outputFile.delete();
                    System.err.println("WARNING [empty file]: " + outputFile.getAbsolutePath() + " has been deleted. The input file " + f + " could be corrupted");
                    GUI.appendLog(this.getLogger(), "\n WARNING [empty file]: " + outputFile.getAbsolutePath() + " has been deleted. The input file " + f + " could be corrupted");
                }
                else {
                    System.err.println("WARNING [missing values]: " + outputFile.getAbsolutePath() + " has missing values. The input file " + f + " could be corrupted or may not exist");
                    GUI.appendLog(this.getLogger(), "\n WARNING [missing values]: " + outputFile.getAbsolutePath() + " has missing values. The input file " + f + " could be corrupted or may not exist");
                }
            }
            else {
                System.err.println("WARNING [file not found]: " + outputFile.getAbsolutePath() + " does not exist. Something went wrong with the input file " + f);
                GUI.appendLog(this.getLogger(), "\n WARNING [file not found]: " + outputFile.getAbsolutePath() + " does not exist. Something went wrong with the input file " + f);
            }
        }
    }
    
}
