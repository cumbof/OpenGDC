package opengdc.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import opengdc.Settings;

public class YAMLreader {
	
    private static HashMap<String, ArrayList<String>> redundantmap = new HashMap<>();

    public static void main(String[]args){
        getMappingAttributes();
    }
		
    public static HashMap<String, ArrayList<String>> getMappingAttributes() {
        // The path of your YAML file.
	String tcga_biospecimen = Settings.getBiospecimenYAML();
	String biospecimen_meta_type = "biospecimen";
        HashMap<String, ArrayList<String>> biospecimen_map = readYAML(tcga_biospecimen, biospecimen_meta_type);
	String tcga_clinical = Settings.getClinicalYAML();
	String clinical_meta_type = "clinical";
        HashMap<String, ArrayList<String>> clinical_map = readYAML(tcga_clinical, clinical_meta_type);
        //HashMap<String, ArrayList<String>> metadata_map = new HashMap<String, ArrayList<String>>(biospecimen_map);
        for (String key: clinical_map.keySet()) {
            ArrayList<String> values = clinical_map.get(key);
            if (biospecimen_map.containsKey(key)) {
                values.addAll(biospecimen_map.get(key));
                //biospecimen_map.remove(key);
            }
            biospecimen_map.put(key, values);
        }
        return biospecimen_map;
    }

    public static HashMap<String, ArrayList<String>> readYAML(String fileName, String meta_type) {
        Yaml yaml = new Yaml();
        try {
            InputStream ios = new FileInputStream(new File(fileName));
            // Parse the YAML file and return the output as a series of Maps and Lists
            Map<String,Object> result = (Map<String,Object>)yaml.load(ios);
            maps(result, meta_type);
        } catch (Exception e) {
            e.printStackTrace();
        }

        HashMap<String, ArrayList<String>> map = new HashMap<String, ArrayList<String>>();
        for (String kk: redundantmap.keySet()) {
            ArrayList<String> val = redundantmap.get(kk);
            if ((kk.endsWith("__path") && kk.contains("__properties")) || !kk.contains("__properties")) { // if is a property i need only the "path" value
                if (kk.endsWith("__path"))
                    kk = kk.replaceAll("__properties", "").replaceAll("__path", "");
                if (kk.contains("__edges_by_property"))
                    kk = kk.replaceAll("__edges_by_property", "");
                if (kk.startsWith("slides"))
                    kk = kk.replaceAll("slides", "cases__samples__portions__slides");
                else if (kk.startsWith("portions"))
                    kk = kk.replaceAll("portions", "cases__samples__portions");
                else if (kk.startsWith("analytes"))
                    kk = kk.replaceAll("analytes", "cases__samples__portions__analytes");
                else if (kk.startsWith("aliquots"))
                    kk = kk.replaceAll("aliquots", "cases__samples__portions__analytes__aliquots");
                else if (kk.startsWith("samples"))
                    kk = kk.replaceAll("samples", "cases__samples");
                else if (kk.startsWith("demographic"))
                    kk = kk.replaceAll("demographic", "cases__demographic");
                else if (kk.startsWith("diagnoses"))
                    kk = kk.replaceAll("diagnoses", "cases__diagnoses");
                else if (kk.startsWith("exposures"))
                    kk = kk.replaceAll("exposures", "cases__exposures");
                if (!map.containsKey(kk))
                    map.put("gdc__"+kk,val);
            }
        }
        System.out.println();
        for (String kk: map.keySet())
            System.out.println(kk+": \n\t"+map.get(kk));
        return map;
    }

    private static void maps(Map<String, Object> result, String meta_type) {
        for (String k : result.keySet()) {
            //System.out.println(k);
            System.out.println(result.get(k).toString());
            String k_print = "";
            if (k.endsWith("s"))
                k_print = k.substring(0,k.length()-2) + "es";
            else if (!k.equals("demographic"))
                k_print = k+"s";
            else k_print = k;

            for (Object s: (ArrayList<Object>)result.get(k)) {
                //System.out.println(s);
                //HashMap<String,Object> h = (HashMap<String,Object>)s;
                //for (String p: h.keySet()) {
                //	System.out.println(p);
                //}
                if (s instanceof HashMap) {
                    HashMap<String,Object> h = (HashMap<String,Object>)s;
                    for (String p: h.keySet()) {
                        //System.out.println(k+"s."+p);
                        if (!p.equals("edge_properties") && !p.equals("edges") && !p.equals("edge_datetime_properties") && !p.equals("root") && !p.equals("generated_id")) {
                            //recursiveRecognition(k+"_"+p, h.get(p), meta_type);
                            //else 							
                            recursiveRecognition(k_print+"__"+p, h.get(p), meta_type);
                        }
                    }
                }
            }
        }		
    }

    private static void recursiveRecognition(String attribute, Object object, String meta_type) {
        if (object instanceof String) {
            //System.out.println(attribute);
            //System.out.println("---"+object);
            ArrayList<String> list = new ArrayList<>();
            if (redundantmap.containsKey(attribute))
                list = redundantmap.get(attribute);
            String val = ((String)object).replaceAll("\\.", "").replaceAll(":", "__").replaceAll("\\|", ""); //.replaceAll("\\[.*\\]", "");
            for (String splitted_val: val.split("\\/")) { //many values are splitted with "/"
                if (!splitted_val.equals("") && splitted_val!=null)
                    list.add( meta_type+"__"+splitted_val);
            }
            redundantmap.put(attribute, list);
        }
        else if (object instanceof HashMap) {
            for (String k: ((HashMap<String,Object>) object).keySet()) {
                //System.out.println(attribute+"."+k);
                Object obj = ((HashMap) object).get(k);
                if (attribute.endsWith("edges_by_property"))
                    k = k.substring(0, k.length()-1);
                recursiveRecognition(attribute+"__"+k, obj, meta_type);
            }
        }
        else if (object instanceof ArrayList) {
            for (Object k: ((ArrayList<Object>) object)) {
                //System.out.println(attribute+"."+k);
                recursiveRecognition(attribute, k, meta_type);
            }
        }
    }
    
}