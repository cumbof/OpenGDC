package opengdc.util;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class FormatUtils {
    
    private static final String END_OF_LINE = "\n";
    private static final String BED_SEPARATOR = "\t";
    private static final String CSV_SEPARATOR = ",";
    private static final String GTF_SEPARATOR = "\t";
    private static String prevContentForJSON = "";
    
    private static HashSet<String> formats = new HashSet<>();
    
    private static void initFormatsList() {
        formats = new HashSet<>();
        formats.add("BED");
        formats.add("GTF");
        formats.add("CSV");
        formats.add("JSON");
        formats.add("XML");
    }
    
    public static HashSet<String> getFormatsList() {
        if (formats.isEmpty()) initFormatsList();
        return formats;
    }
    
    /*-------------------------------------------------------*/
    
    public static String initDocument(String formatExt) {
        if (formatExt.toLowerCase().equals("json"))
            return initJSON();
        else if (formatExt.toLowerCase().equals("xml"))
            return initXML();
        return "";
    }
    
    private static String initJSON() {
        return "{" + END_OF_LINE + "\t\"aliquot\": {" + END_OF_LINE + "\t\t\"data\": [" + END_OF_LINE;
    }
    
    private static String initXML() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + END_OF_LINE + "<aliquot>" + END_OF_LINE;
    }
    
    /*-------------------------------------------------------*/
    
    public static String endDocument(String formatExt) {
        if (formatExt.toLowerCase().equals("json"))
            return endJSON();
        else if (formatExt.toLowerCase().equals("xml"))
            return endXML();
        return "";
    }
    
    private static String endJSON() {
        String str = "";
        if (!prevContentForJSON.trim().equals("")) {
            int lastCommaIndex = prevContentForJSON.lastIndexOf(",");
            if (lastCommaIndex > 0)
                str = new StringBuilder(prevContentForJSON).replace(lastCommaIndex, lastCommaIndex+1, "").toString();
        }
        return str + "\t\t]" + END_OF_LINE + "\t}" + END_OF_LINE + "}";
    }
    
    private static String endXML() {
        return "</aliquot>";
    }
    
    /*-------------------------------------------------------*/
    
    public static String createEntry(String formatExt, ArrayList<String> values, String[] doc_header, boolean firstRow) {
        if (formatExt.toLowerCase().equals("bed"))
            return strBED(values, firstRow);
        else if (formatExt.toLowerCase().equals("csv"))
            return strCSV(values, firstRow);
        else if (formatExt.toLowerCase().equals("json"))
            return strJSON(values, doc_header);
        else if (formatExt.toLowerCase().equals("xml"))
            return strXML(values, doc_header);
        else if (formatExt.toLowerCase().equals("gtf"))
            return strGTF(values, doc_header, firstRow);
        return "";
    }

    private static String strBED(ArrayList<String> values, boolean firstRow) {
        if (!values.isEmpty()) {
            String line = "";
            for (int i=0; i<values.size()-1; i++)
                line = line + values.get(i) + BED_SEPARATOR;
            line = line + values.get(values.size()-1);
            if (firstRow)
                return line;
            return END_OF_LINE + line;
            //return line + END_OF_LINE;
        }
        return "";
    }
    
    private static String strCSV(ArrayList<String> values, boolean firstRow) {
        if (!values.isEmpty()) {
            String line = "";
            for (int i=0; i<values.size()-1; i++)
                line = line + values.get(i) + CSV_SEPARATOR;
            line = line + values.get(values.size()-1);
            if (firstRow)
                return line;
            return END_OF_LINE + line;
            //return line + END_OF_LINE;
        }
        return "";
    }
    
    private static String strJSON(ArrayList<String> values, String[] doc_header) {
        String str = "\t\t\t{" + END_OF_LINE;
        int index = 0;
        for (int i=0; i<values.size(); i++) {
            String header = doc_header[index];
            str = str + "\t\t\t\t\""+header+"\": \""+values.get(i)+"\"";
            if ((i+1)>=values.size())
                str = str + END_OF_LINE;
            else
                str = str + "," + END_OF_LINE;
            index++;
        }
        str = str + "\t\t\t}" + "," + END_OF_LINE;
        
        String strToReturn = prevContentForJSON;
        prevContentForJSON = str;
        return strToReturn;
    }

    private static String strXML(ArrayList<String> values, String[] doc_header) {
        String str = "\t<data>" + END_OF_LINE;
        int index = 0;
        for (String val: values) {
            String header = doc_header[index];
            str = str + "\t\t<"+header+">" + val + "</"+header+">" + END_OF_LINE;
            index++;
        }
        str = str + "\t</data>" + END_OF_LINE;
        return str;
    }
    
    private static String strGTF(ArrayList<String> values, String[] doc_header, boolean firstRow) {
        if (!values.isEmpty()) {
            String chr = values.get(0);
            String source = "OpenGDC";
            String feature = "Region";
            String start = values.get(1);
            String end = values.get(2);
            String score = ".";
            String strand = (values.get(3).trim().equals("*")) ? "." : values.get(3);
            String frame = ".";
            
            String line = chr + GTF_SEPARATOR + 
                          source + GTF_SEPARATOR + 
                          feature + GTF_SEPARATOR + 
                          start + GTF_SEPARATOR + 
                          end + GTF_SEPARATOR + 
                          score + GTF_SEPARATOR + 
                          strand + GTF_SEPARATOR + 
                          frame + GTF_SEPARATOR;
            
            // skip first 4 elements            
            for (int i=0; i<values.size(); i++) {
                if (i > 3) {
                    line = line + doc_header[i] + " " + "\""+values.get(i)+"\"" + "; ";
                }
            }
            if (firstRow)
                return line;
            return END_OF_LINE + line;
            //return line + END_OF_LINE;
        }
        return "";
    }
    
    /*-------------------------------------------------------*/
    
    public static String generateDataSchema(String[] header, String[] types) {
        String schema = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
                        "<gmqlSchemaCollection xmlns=\"http://genomic.elet.polimi.it/entities\" name=\"GLOBAL_SCHEMAS\">\n" +
                        "\t<gmqlSchema type=\"tab\" coordinate_system=\"1-based\">\n";
        for (int i=0; i<header.length; i++)
            schema += "\t\t<field type=\""+types[i]+"\">"+header[i]+"</field>\n";
        schema += "\t</gmqlSchema>\n"+
                  "</gmqlSchemaCollection>";
        return schema;
    }
    
}
