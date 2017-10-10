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

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import opengdc.util.FormatUtils;

/**
 *
 * @author fabio
 */
public abstract class BioParser {
    
    public HashSet<String> acceptedInputFileFormats = new HashSet<>();
    
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
    
    public void printData(Path outFilePath, HashMap<String, HashMap<String, ArrayList<ArrayList<String>>>> dataMap, String format, String[] header) {
        if (!dataMap.isEmpty()) {
            ArrayList<String> chrs = new ArrayList<>(dataMap.keySet());
            // sort by chr
            Collections.sort(chrs);
            for (String chr: chrs) {
                HashMap<String, ArrayList<ArrayList<String>>> dataList = dataMap.get(chr);
                // sort by start position
                ArrayList<String> starts = new ArrayList<>(dataList.keySet());
                Collections.sort(starts);
                for (String start: starts) {
                    ArrayList<ArrayList<String>> dataArray = dataList.get(start);
                    for (ArrayList<String> data: dataArray) {
                        try {
                            Files.write(outFilePath, (FormatUtils.createEntry(format, data, header)).getBytes("UTF-8"), StandardOpenOption.APPEND);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    
}
