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
import opengdc.util.GDCQuery;
import opengdc.util.MetadataHandler;

/**
 *
 * @author fabio
 */
public class RetrieveExpInfoFromAttribute {
    
    // mapping endpoint
    // https://api.gdc.cancer.gov/files/_mapping
    // https://api.gdc.cancer.gov/cases/_mapping
    
    public static void main(String[] args) {
        String aliquot_uuid = "TARGET-40-PANVJJ-01A-01D";
        HashMap<String, HashMap<String, Boolean>> additional_attributes = MetadataHandler.getAdditionalAttributes("cases");
        HashMap<String, Boolean> add_attr_tmp = additional_attributes.get("manually_curated");
        add_attr_tmp.put("samples.portions.analytes.aliquots.aliquot_id", false);
        ArrayList<String> additional_attributes_sorted = new ArrayList<>(additional_attributes.keySet());
        Collections.sort(additional_attributes_sorted);
        for (String metakey: additional_attributes_sorted) {
            ArrayList<HashMap<String, String>> files_info = GDCQuery.retrieveExpInfoFromAttribute("cases", "samples.portions.analytes.aliquots.submitter_id", aliquot_uuid, new HashSet<>(additional_attributes.get(metakey).keySet()), 0, 0, null);
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
