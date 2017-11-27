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

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import opengdc.GUI;
import opengdc.Settings;
import opengdc.util.FSUtils;
import opengdc.util.GDCData;
import opengdc.util.GDCQuery;
import opengdc.util.MetadataHandler;
import opengdc.util.XMLNode;

/**
 *
 * @author fabio
 */
public class MetadataParser extends BioParser {

    private ArrayList<XMLNode> aliquotNodes = new ArrayList<>();
    
    @Override
    public int convert(String program, String disease, String dataType, String inPath, String outPath) {
        int acceptedFiles = FSUtils.acceptedFilesInFolder(inPath, getAcceptedInputFileFormats());
        System.err.println("Data Amount: " + acceptedFiles + " files" + "\n\n");
        GUI.appendLog("Data Amount: " + acceptedFiles + " files" + "\n\n");
        
        if (acceptedFiles == 0)
            return 1;
        
        HashMap<String, HashMap<String, String>> clinicalBigMap = new HashMap<>();
        HashMap<String, HashMap<String, String>> biospecimenBigMap = new HashMap<>();
        
        File[] files = (new File(inPath)).listFiles();
        for (File f: files) {
            // remove all elements in aliquotNodes from previous iterations
            aliquotNodes = new ArrayList<>();
            if (f.isFile()) {
                String extension = FSUtils.getFileExtension(f);
                if (getAcceptedInputFileFormats().contains(extension)) {
                    System.err.println("Processing " + f.getName());
                    GUI.appendLog("Processing " + f.getName() + "\n");
                    
                    HashMap<String, Object> metadata_from_xml = MetadataHandler.getXMLMap(f.getAbsolutePath());
                    if (f.getName().toLowerCase().contains("clinical")) {                        
                        HashMap<String, String> clinical_metadata_map = MetadataHandler.getDataMap(metadata_from_xml, null);
                        String patient_uuid = MetadataHandler.findKey(clinical_metadata_map, "endsWith", "bcr_patient_uuid");
                        clinicalBigMap.put(patient_uuid, clinical_metadata_map);
                    }
                    else if (f.getName().toLowerCase().contains("biospecimen")) {                        
                        XMLNode root = new XMLNode();
                        root.setRoot();
                        root = convertMapToIndexedTree(metadata_from_xml, root);
                        searchForAliquots(root);
                        
                        HashMap<String, HashMap<String, String>> aliquot2attributes = new HashMap<>();
                        for (XMLNode aliquot: aliquotNodes) {
                            String aliquot_uuid = MetadataHandler.findKey(aliquot.getAttributes(), "endsWith", "bcr_aliquot_uuid");
                            if (!aliquot_uuid.equals("")) {
                                HashMap<String, String> aliquotMeta = aliquot.getAttributes();
                                HashMap<String, String> parentMeta = extractParentMetadata(aliquot.getParent(), new HashMap<>());
                                aliquotMeta.putAll(parentMeta);
                                aliquot2attributes.put(aliquot_uuid, aliquotMeta);
                            }
                        }
                        
                        if (!aliquot2attributes.isEmpty()) {
                            for (String aliquot_uuid: aliquot2attributes.keySet())
                                biospecimenBigMap.put(aliquot_uuid, aliquot2attributes.get(aliquot_uuid));
                        }
                    }
                }
            }
        }
        
        // merge clinical and biospecimen info (plus additional metadata)
        if (!clinicalBigMap.isEmpty() || !biospecimenBigMap.isEmpty()) {
            HashMap<String, HashMap<String, Boolean>> additional_attributes = getAdditionalAttributes();
            for (String aliquot_uuid: biospecimenBigMap.keySet()) {
                try {
                    // print biospecimen info
                    String patient_uuid = "";
                    for (String attribute: biospecimenBigMap.get(aliquot_uuid).keySet()) {
                        if (MetadataHandler.getAttributeFromKey(attribute).trim().toLowerCase().equals("shared:bcr_patient_uuid")) {
                            patient_uuid = biospecimenBigMap.get(aliquot_uuid).get(attribute);
                            break;
                        }
                    }
                    HashMap<String, String> biospecimen_map = new HashMap<>();
                    for (String attr: biospecimenBigMap.get(aliquot_uuid).keySet())
                        biospecimen_map.put(MetadataHandler.getAttributeFromKey(attr).replaceAll(":","__"), biospecimenBigMap.get(aliquot_uuid).get(attr));
                    ArrayList<String> biospecimen_sorted = new ArrayList<>(biospecimen_map.keySet());
                    Collections.sort(biospecimen_sorted);
                    
                    // print clinical info
                    ArrayList<String> clinical_sorted = new ArrayList<>();
                    HashMap<String, String> clinical_map = new HashMap<>();
                    if (!patient_uuid.equals("")) {
                        if (clinicalBigMap.containsKey(patient_uuid)) {
                            for (String attr: clinicalBigMap.get(patient_uuid).keySet())
                                clinical_map.put(MetadataHandler.getAttributeFromKey(attr).replaceAll(":","__"), clinicalBigMap.get(patient_uuid).get(attr));
                            clinical_sorted = new ArrayList<>(clinical_map.keySet());
                            Collections.sort(clinical_sorted);
                        }
                    }
                    
                    // generate manually curated metadata
                    if (!additional_attributes.isEmpty()) {
                        ArrayList<String> additional_attributes_sorted = new ArrayList<>(additional_attributes.keySet());
                        Collections.sort(additional_attributes_sorted);
                        for (String metakey: additional_attributes_sorted) {
                            ArrayList<HashMap<String, String>> files_info = GDCQuery.retrieveExpInfoFromAttribute("cases.samples.portions.analytes.aliquots.aliquot_id", aliquot_uuid.toLowerCase(), new HashSet<>(additional_attributes.get(metakey).keySet()), 0);
                            ArrayList<HashMap<String, String>> aggregated_files_info = aggregateSameDataTypeInfo(files_info, getAggregatedAdditionalAttributes());
                            for (HashMap<String, String> file_info: aggregated_files_info) {
                                if (file_info != null) {
                                    // handle missing required attributes
                                    HashSet<String> missing_required_attributes = new HashSet<>();
                                    HashMap<String, String> manually_curated = new HashMap<>();
                                    HashMap<String, Boolean> attribute2required = additional_attributes.get(metakey);
                                    ArrayList<String> file_info_sorted = new ArrayList<>(file_info.keySet());
                                    Collections.sort(file_info_sorted);
                                    for (String attribute: file_info_sorted) {
                                        String attribute_parsed = FSUtils.stringToValidJavaIdentifier(metakey + "__" + attribute.replaceAll("\\.", "__"));
                                        /*************************************************************/
                                        /** patch for the attribute 'manually_curated__data_format' **/
                                        if (attribute_parsed.trim().toLowerCase().equals("manually_curated__data_format"))
                                            attribute_parsed = "manually_curated__source_data_format";
                                        /*************************************************************/
                                        String value_parsed = checkForNAs(file_info.get(attribute));
                                        if (!value_parsed.trim().equals(""))
                                            manually_curated.put(attribute_parsed, value_parsed);
                                        else {
                                            for (String attr: attribute2required.keySet()) {
                                                if (attr.toLowerCase().equals(attribute.toLowerCase())) {
                                                    if (attribute2required.get(attr)) // if attribute is required
                                                        missing_required_attributes.add(attribute_parsed);
                                                }
                                            }
                                        }
                                    }
                                    
                                    // generate additional manually curated metadata
                                    String manually_curated_data_type = "";
                                    for (String mcattr: manually_curated.keySet()) {
                                        if (mcattr.toLowerCase().contains("data_type")) {
                                            manually_curated_data_type = manually_curated.get(mcattr);
                                            break;
                                        }
                                    }
                                    // create a suffix to append to the aliquot id
                                    String suffix_id = this.getOpenGDCSuffix(manually_curated_data_type, false);

                                    HashMap<String, HashMap<String, Object>> additional_manually_curated = getAdditionalManuallyCuratedAttributes(program, disease, dataType, aliquot_uuid, biospecimenBigMap.get(aliquot_uuid), clinicalBigMap.get(patient_uuid), manually_curated, suffix_id);
                                    if (!additional_manually_curated.isEmpty()) {
                                        for (String attr: additional_manually_curated.keySet()) {
                                            String attribute_parsed = FSUtils.stringToValidJavaIdentifier(attr);
                                            HashMap<String, Object> values = additional_manually_curated.get(attr);
                                            if (!values.isEmpty()) {
                                                String value_parsed = checkForNAs((String)additional_manually_curated.get(attr).get("value"));
                                                if (!value_parsed.trim().equals(""))
                                                    manually_curated.put(attribute_parsed, value_parsed);
                                                else {
                                                    if ((Boolean)additional_manually_curated.get(attr).get("required")) // if attribute is required
                                                        missing_required_attributes.add(attr);
                                                }
                                            }
                                        }
                                    }
                                    
                                    if (!manually_curated_data_type.equals("")) {
                                        FileOutputStream fos = new FileOutputStream(outPath + aliquot_uuid.toLowerCase() + "-" + suffix_id + "." + this.getFormat());
                                        PrintStream out = new PrintStream(fos);

                                        // biospecimen
                                        for (String attribute: biospecimen_sorted) {
                                            String attribute_parsed = FSUtils.stringToValidJavaIdentifier("biospecimen__" + attribute);
                                            String value_parsed = checkForNAs(biospecimen_map.get(attribute));
                                            if (!value_parsed.trim().equals(""))
                                                out.println(attribute_parsed + "\t" + value_parsed);
                                        }

                                        // clinical
                                        for (String attribute: clinical_sorted) {
                                            String attribute_parsed = FSUtils.stringToValidJavaIdentifier("clinical__" + attribute);
                                            String value_parsed = checkForNAs(clinical_map.get(attribute));
                                            if (!value_parsed.trim().equals(""))
                                                out.println(attribute_parsed + "\t" + value_parsed);
                                        }

                                        // generate audit_warning
                                        if (!missing_required_attributes.isEmpty()) {
                                            String missed_attributes_list = "";
                                            for (String ma: missing_required_attributes)
                                                missed_attributes_list += ma+", ";
                                            manually_curated.put("manually_curated__audit_warning", "missed the following required metadata: ["+missed_attributes_list.substring(0, missed_attributes_list.length()-2)+"]");
                                        }
                                        
                                        // sort and print manually_curated attributes
                                        ArrayList<String> manually_curated_attributes_sorted = new ArrayList<>(manually_curated.keySet());
                                        Collections.sort(manually_curated_attributes_sorted);
                                        for (String attr: manually_curated_attributes_sorted)
                                            out.println(attr + "\t" + manually_curated.get(attr));

                                        out.close();
                                        fos.close();
                                    }
                                    
                                }
                            }
                        }
                    }
                    
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return 0;
    }

    private ArrayList<HashMap<String, String>> aggregateSameDataTypeInfo(ArrayList<HashMap<String, String>> files_info, ArrayList<String> aggregatedAdditionalAttributes) {
        HashMap<String, ArrayList<HashMap<String, String>>> aggregated = new HashMap<>();
        String platform_tmp = "";
        for (HashMap<String, String> file_info: files_info) {
            if (file_info != null) {
                if (file_info.containsKey("data_type")) {
                    String data_type = file_info.get("data_type");
                    ArrayList<HashMap<String, String>> values = new ArrayList<>();
                    if (aggregated.containsKey(data_type))
                        values = aggregated.get(data_type);
                    values.add(file_info);
                    aggregated.put(data_type, values);
                    if (data_type.trim().toLowerCase().equals("aligned reads")) {
                        if (file_info.containsKey("platform"))
                            platform_tmp = file_info.get("platform");
                    }
                }
            }
        }

        ArrayList<HashMap<String,String>> compressedMap = new ArrayList<>();
        for(String key: aggregated.keySet()){
            HashMap<String, String> tmp = new HashMap<>();
            ArrayList<HashMap<String, String>> mapList = aggregated.get(key);
            for (HashMap<String, String> map: mapList){
                for (String attribute: map.keySet()){
                    if (!tmp.containsKey(attribute))
                        tmp.put(attribute, map.get(attribute));
                    else {
                        if (aggregatedAdditionalAttributes.contains(attribute)) {
                            String value = tmp.get(attribute);
                            value = value + "," + map.get(attribute);
                            tmp.put(attribute, value);
                        }
                    }
                }
            }

            // platform control
            // if platform does not exist or is empty
            // set the same platform of the Aligned Reads
            if (!tmp.containsKey("platform"))
                tmp.put("platform", platform_tmp);
            else {
                if (tmp.get("platform").trim().equals(""))
                    tmp.put("platform", platform_tmp);
            }
            
            // populate compressedMap
            compressedMap.add(tmp);
        }

        return compressedMap;
    }
    
    // the attributes in this methods are all required 
    private HashMap<String, HashMap<String, Object>> getAdditionalManuallyCuratedAttributes(String program, String disease, String dataType, String aliquot_uuid, HashMap<String, String> biospecimen_attributes, HashMap<String, String> clinical_attributes, HashMap<String, String> manually_curated, String suffix_id) {
        String attributes_prefix = "manually_curated";
        String category_separator = "__";
        
        /******* tissue_status *******/
        // retrieve 'manually_curated__tissue_status' from 'biospecimen__bio__sample_type_id'
        HashMap<String, HashMap<String, Object>> additional_attributes = new HashMap<>();
        String tissue_id = "";
        for (String bio_attr: biospecimen_attributes.keySet()) {
            if (bio_attr.trim().toLowerCase().contains("sample_type_id")) {
                tissue_id = biospecimen_attributes.get(bio_attr);
                break;
            }
        }
        HashMap<String, Object> values = new HashMap<>();
        String tissue_status = "";
        if (!tissue_id.trim().equals(""))
            tissue_status = getTissueStatus(tissue_id);
        values.put("value", tissue_status);
        values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"tissue_status", values);
        
        /******* exp_data_bed_url *******/
        values = new HashMap<>();
        String expDataType = "";
        for (String man_attr: manually_curated.keySet()) {
            if (man_attr.trim().toLowerCase().contains("data_type")) {
                expDataType = manually_curated.get(man_attr);
                break;
            }
        }
        if (!expDataType.trim().equals("")) {
            if (GDCData.getGDCData2FTPFolderName().containsKey(expDataType.trim().toLowerCase())) {
                String opengdc_data_folder_name = GDCData.getGDCData2FTPFolderName().get(expDataType.trim().toLowerCase());
                expDataType = Settings.getOpenGDCFTPRepoProgram(program, false)+disease.trim().toLowerCase()+"/"+opengdc_data_folder_name+"/"+aliquot_uuid.trim().toLowerCase()+"-"+suffix_id+"."+Settings.getOpenGDCFTPConvertedDataFormat();
            }
            else expDataType = "";
        }
        values.put("value", expDataType);
        values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"exp_data_"+Settings.getOpenGDCFTPConvertedDataFormat()+"_url", values);
        
        /******* opengdc_id *******/
        values = new HashMap<>();
        String opengdcId = "";
        opengdcId = aliquot_uuid.trim().toLowerCase()+"-"+suffix_id;
        values.put("value", opengdcId);
        values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"opengdc_id", values);

        /******* data_format *******/
        values = new HashMap<>();
        String data_format = "";
        if (!expDataType.trim().equals(""))
            data_format = Settings.getOpenGDCFTPConvertedDataFormat().toUpperCase();
        values.put("value", data_format);
        values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"data_format", values);
        
        /******* exp_metadata_url *******/
        values = new HashMap<>();
        values.put("value", Settings.getOpenGDCFTPRepoProgram(program, false)+disease.trim().toLowerCase()+"/"+GDCData.getGDCData2FTPFolderName().get(dataType.trim().toLowerCase())+"/"+aliquot_uuid.trim().toLowerCase()+"-"+suffix_id+"."+this.getFormat());
        values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"exp_metadata_url", values);
                
        return additional_attributes;
    }
    
    private HashMap<String, String> extractParentMetadata(XMLNode node, HashMap<String, String> meta) {
        if (meta == null)
            meta = new HashMap<>();
        meta.putAll(node.getAttributes());
        
        if (node.getParent() == null)
            return meta;
        return extractParentMetadata(node.getParent(), meta);
    }
    
    private void searchForAliquots(XMLNode root) {
        if (root.getLabel().toLowerCase().trim().endsWith("bio:aliquot") && root.getAttributes().size()>0) {
            aliquotNodes.add(root);
            //System.err.println(root.getLabel() + "\t" + "attributes: " + root.getAttributes().size());
        }
        else {
            if (root.hasChilds()) {
                for (XMLNode child: root.getChilds())
                    searchForAliquots(child);
            }
        }
    }
    
    private XMLNode convertMapToIndexedTree(HashMap<String, Object> map, XMLNode root) {
        for (String k: map.keySet()) {
            if (map.get(k) instanceof HashMap) {
                XMLNode child = new XMLNode();
                child.setLabel(k);
                child.setParent(root);
                root.addChild(convertMapToIndexedTree((HashMap<String, Object>)map.get(k), child));
            }
            else if (map.get(k) instanceof String) {
                String value = (String)map.get(k);
                root.addAttribute(k, value);
            }
        }
        return root;
    }
    
    private String checkForNAs(String metaValue) {
        if (metaValue.trim().toLowerCase().equals("na") || metaValue.trim().toLowerCase().equals("null"))
            return "";
        else return metaValue;
    }

    public static ArrayList<String> getAggregatedAdditionalAttributes() {
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("file_id");
        attributes.add("file_name");
        attributes.add("file_size");
        attributes.add("analysis.analysis_id");
        attributes.add("analysis.workflow_type");

        return attributes;
    }
    
    public static HashMap<String, HashMap<String, Boolean>> getAdditionalAttributes() {
        HashMap<String, HashMap<String, Boolean>> additionalAttributes = new HashMap<>();
        // <'attribute:string', 'required:boolean'>
        HashMap<String, Boolean> attributes = new HashMap<>();
        attributes.put("data_category", false);
        attributes.put("data_format", false);
        attributes.put("data_type", true);
        attributes.put("experimental_strategy", false);
        attributes.put("file_id", true);
        attributes.put("file_name", false);
        attributes.put("file_size", false);
        attributes.put("platform", false);
        attributes.put("analysis.analysis_id", false);
        attributes.put("analysis.workflow_link", false);
        attributes.put("analysis.workflow_type", false);
        attributes.put("cases.case_id", false);
        attributes.put("cases.disease_type", true);
        attributes.put("cases.primary_site", false);
        attributes.put("cases.demographic.year_of_birth", false);
        attributes.put("cases.project.program.program_id", false);
        attributes.put("cases.project.program.name", false);
                
        // other gdc attributes
        attributes.put("cases.submitter_id", false);
        attributes.put("cases.samples.tumor_descriptor", false);
        attributes.put("cases.samples.tissue_type", false);
        //attributes.put("cases.samples.sample_type", false);
        //attributes.put("cases.samples.submitter_id", false);
        //attributes.put("cases.samples.sample_id", false);
        //attributes.put("cases.samples.portions.analytes.aliquots.aliquot_id", false);
        //attributes.put("cases.samples.portions.analytes.aliquots.submitter_id", false);
        
        additionalAttributes.put("manually_curated", attributes);
        return additionalAttributes;
    }

    @Override
    public String[] getHeader() {
        return null;
    }

    @Override
    public String[] getAttributesType() {
        return null;
    }

    @Override
    public void initAcceptedInputFileFormats() {
        this.acceptedInputFileFormats = new HashSet<>();
        this.acceptedInputFileFormats.add(".xml");
    }
    
    private String getTissueStatus(String tissue_id) {
        try {
            if (tissue_id.startsWith("0"))
                return "tumoral";
            else if (tissue_id.startsWith("1"))
                return "normal";
            else if (tissue_id.startsWith("2"))
                return "control";
            else
                return "undefined";
        } catch (Exception e) {
            return "undefined";
        }
    }
    
}