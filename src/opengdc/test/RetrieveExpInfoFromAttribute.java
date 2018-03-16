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
        String submitter_id = "TARGET-40-PANVJJ-01A-01D";
        HashMap<String, HashMap<String, Boolean>> additional_attributes = MetadataHandler.getAdditionalAttributes("cases");
        HashMap<String, Boolean> add_attr_tmp = additional_attributes.get("manually_curated");
        add_attr_tmp.put("samples.portions.analytes.aliquots.aliquot_id", false);
        add_attr_tmp.put("samples.portions.analytes.aliquots.submitter_id", false);
        ArrayList<String> additional_attributes_sorted = new ArrayList<>(additional_attributes.keySet());
        Collections.sort(additional_attributes_sorted);
        for (String metakey: additional_attributes_sorted) {
            HashSet<String> additional_attributes_tmp = new HashSet<>(additional_attributes.get(metakey).keySet());
            ArrayList<HashMap<String, ArrayList<Object>>> files_info = GDCQuery.retrieveExpInfoFromAttribute("cases", "samples.portions.analytes.aliquots.submitter_id", submitter_id, additional_attributes_tmp, 0, 0, null);
            System.err.println("files_info: "+files_info.size());
            if (!files_info.isEmpty()) {
                HashMap<String, String> files_info_res = new HashMap<>();
                for (HashMap<String, ArrayList<Object>> file_info: files_info) {
                    for (String k: file_info.keySet()) {
                        for (Object obj: file_info.get(k)) {
                            HashMap<String, Object> map = (HashMap<String, Object>)obj;
                            for (String kmap: map.keySet()) {
                                try {
                                    boolean contains_submitter_id = false;
                                    if (kmap.toLowerCase().equals("aliquot_id")) {
                                        if (map.containsKey("submitter_id"))
                                            contains_submitter_id = true;
                                    }
                                    //System.err.println("---> "+contains_submitter_id);
                                    
                                    if (contains_submitter_id) {
                                        if (String.valueOf(map.get("submitter_id")).toLowerCase().equals(submitter_id.toLowerCase())) {
                                            files_info_res.put("samples.portions.analytes.aliquots.aliquot_id", String.valueOf(map.get("aliquot_id")));
                                            files_info_res.put("samples.portions.analytes.aliquots.submitter_id", String.valueOf(map.get("submitter_id")));
                                            additional_attributes_tmp.remove("samples.portions.analytes.aliquots.submitter_id");
                                        }
                                    }
                                    else {
                                        String add_attr_curr = "";
                                        for (String add_attr: additional_attributes_tmp) {
                                            String[] add_attr_split = add_attr.split("\\.");
                                            String last_val = add_attr_split[add_attr_split.length-1];
                                            if (last_val.toLowerCase().equals(kmap.toLowerCase())) {
                                                files_info_res.put(add_attr, String.valueOf(map.get(kmap)));
                                                add_attr_curr = add_attr;
                                                break;
                                            }
                                        }
                                        if (!add_attr_curr.trim().equals(""))
                                            additional_attributes_tmp.remove(add_attr_curr);
                                    }
                                }
                                catch (Exception e) { 
                                    e.printStackTrace();
                                }
                                /*try {
                                    System.err.println(kmap+": "+String.valueOf(map.get(kmap)));
                                }
                                catch (Exception e) {}*/
                            }
                        }
                        
                    }
                    //System.err.println("-----------");
                }
                
                for (String key: files_info_res.keySet())
                    System.err.println(key+": "+files_info_res.get(key));
                System.err.println("-----------");
            }
        }
    }
    
}
