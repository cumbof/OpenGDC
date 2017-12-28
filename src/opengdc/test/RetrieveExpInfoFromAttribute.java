/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package opengdc.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import opengdc.parser.MetadataParser;
import opengdc.util.GDCQuery;

/**
 *
 * @author fabio
 */
public class RetrieveExpInfoFromAttribute {
    
    public static void main(String[] args) {
        String aliquot_uuid = "0AAB0660-F928-4EE0-86FB-583E0F3F42D2";
        HashMap<String, HashMap<String, Boolean>> additional_attributes = MetadataParser.getAdditionalAttributes();
        ArrayList<String> additional_attributes_sorted = new ArrayList<>(additional_attributes.keySet());
        Collections.sort(additional_attributes_sorted);
        for (String metakey: additional_attributes_sorted) {
            ArrayList<HashMap<String, String>> files_info = GDCQuery.retrieveExpInfoFromAttribute("cases.samples.portions.analytes.aliquots.aliquot_id", aliquot_uuid.toLowerCase(), new HashSet<>(additional_attributes.get(metakey).keySet()), 0, 0, null);
            System.err.println("files_info: "+files_info.size());
            if (!files_info.isEmpty()) {
                for (HashMap<String, String> file_info: files_info) {
                    for (String k: file_info.keySet())
                        System.err.println(k+": "+file_info.get(k));
                    System.err.println("-----------");
                }
            }
        }
    }
    
}
