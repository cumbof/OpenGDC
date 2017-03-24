/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gdc2bed.parser;

import java.util.HashSet;

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
    
}
