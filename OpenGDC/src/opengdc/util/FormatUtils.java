/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.util;

import java.util.HashSet;

/**
 *
 * @author fabio
 */
public class FormatUtils {
    
    private static HashSet<String> formats = new HashSet<>();
    
    private static void initFormatsList() {
        formats = new HashSet<>();
        formats.add("BED");
        formats.add("GTF");
        formats.add("CSV");
        formats.add("JSON");
    }
    
    public static HashSet<String> getFormatsList() {
        if (formats.isEmpty()) initFormatsList();
        return formats;
    }
    
}
