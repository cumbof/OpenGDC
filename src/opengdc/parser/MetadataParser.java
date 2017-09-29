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
import opengdc.util.FSUtils;
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
                    FileOutputStream fos = new FileOutputStream(outPath + aliquot_uuid.toLowerCase() + "." + this.getFormat());
                    PrintStream out = new PrintStream(fos);
                    // handle missing required attributes
                    boolean missing_required_attributes = false;
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
                    for (String attribute: biospecimen_sorted) {
                        String attribute_parsed = FSUtils.stringToValidJavaIdentifier("biospecimen__" + attribute);
                        String value_parsed = checkForNAs(biospecimen_map.get(attribute));
                        if (!value_parsed.trim().equals(""))
                            out.println(attribute_parsed + "\t" + value_parsed);
                    }
                    // print clinical info
                    if (!patient_uuid.equals("")) {
                        if (clinicalBigMap.containsKey(patient_uuid)) {
                            HashMap<String, String> clinical_map = new HashMap<>();
                            for (String attr: clinicalBigMap.get(patient_uuid).keySet())
                                clinical_map.put(MetadataHandler.getAttributeFromKey(attr).replaceAll(":","__"), clinicalBigMap.get(patient_uuid).get(attr));
                            ArrayList<String> clinical_sorted = new ArrayList<>(clinical_map.keySet());
                            Collections.sort(clinical_sorted);
                            for (String attribute: clinical_sorted) {
                                String attribute_parsed = FSUtils.stringToValidJavaIdentifier("clinical__" + attribute);
                                String value_parsed = checkForNAs(clinical_map.get(attribute));
                                if (!value_parsed.trim().equals(""))
                                    out.println(attribute_parsed + "\t" + value_parsed);
                            }
                        }
                    }
                    
                    HashMap<String, String> manually_curated = new HashMap<>();
                    // generate manually curated metadata
                    if (!additional_attributes.isEmpty()) {
                        ArrayList<String> additional_attributes_sorted = new ArrayList<>(additional_attributes.keySet());
                        Collections.sort(additional_attributes_sorted);
                        for (String metakey: additional_attributes_sorted) {
                            HashMap<String, String> file_info = GDCQuery.retrieveExpInfoFromAttribute("cases.samples.portions.analytes.aliquots.aliquot_id", aliquot_uuid.toLowerCase(), new HashSet<>(additional_attributes.get(metakey).keySet()));
                            if (file_info != null) {
                                HashMap<String, Boolean> attribute2required = additional_attributes.get(metakey);
                                ArrayList<String> file_info_sorted = new ArrayList<>(file_info.keySet());
                                Collections.sort(file_info_sorted);
                                for (String attribute: file_info_sorted) {
                                    String attribute_parsed = FSUtils.stringToValidJavaIdentifier(metakey + "__" + attribute.replaceAll("\\.", "__"));
                                    String value_parsed = checkForNAs(file_info.get(attribute));
                                    if (!value_parsed.trim().equals("")) {
                                        //out.println(attribute_parsed + "\t" + value_parsed);
                                        manually_curated.put(attribute_parsed, value_parsed);
                                    }
                                    else {
                                        if (attribute2required.get(attribute)) // if attribute is required
                                            missing_required_attributes = true;
                                    }
                                }
                            }
                        }
                    }
                    // generate additional manually curated metadata
                    HashMap<String, HashMap<String, Object>> additional_manually_curated = getAdditionalManuallyCuratedAttributes(biospecimenBigMap.get(aliquot_uuid));
                    if (!additional_manually_curated.isEmpty()) {
                        for (String attr: additional_manually_curated.keySet()) {
                            String attribute_parsed = FSUtils.stringToValidJavaIdentifier(attr);
                            HashMap<String, Object> values = additional_manually_curated.get(attr);
                            if (!values.isEmpty()) {
                                String value_parsed = checkForNAs((String)additional_manually_curated.get(attr).get("value"));
                                if (!value_parsed.trim().equals("")) {
                                    //out.println(attribute_parsed + "\t" + value_parsed);
                                    manually_curated.put(attribute_parsed, value_parsed);
                                }
                                else {
                                    if ((Boolean)additional_manually_curated.get(attr).get("required")) // if attribute is required
                                        missing_required_attributes = true;
                                }
                            }
                        }
                    }
                    // generate audit_warning
                    if (missing_required_attributes)
                        manually_curated.put("manually_curated__audit_warning", "missed required metadata");
                    // sort and print manually_curated attributes
                    ArrayList<String> manually_curated_attributes_sorted = new ArrayList<>(manually_curated.keySet());
                    Collections.sort(manually_curated_attributes_sorted);
                    for (String attr: manually_curated_attributes_sorted) {
                        out.println(attr + "\t" + manually_curated.get(attr));
                    }
                    
                    out.close();
                    fos.close();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        
        return 0;
    }
    
    // the attributes in this methods are all required 
    private HashMap<String, HashMap<String, Object>> getAdditionalManuallyCuratedAttributes(HashMap<String, String> biospecimen_attributes) {
        // retrieve 'manually_curated__tissue_status' from 'biospecimen__bio__sample_type_id'
        String attributes_prefix = "manually_curated";
        String category_separator = "__";
        
        HashMap<String, HashMap<String, Object>> additional_attributes = new HashMap<>();
        String tissue_id = "";
        for (String bio_attr: biospecimen_attributes.keySet()) {
            if (bio_attr.trim().toLowerCase().contains("sample_type_id")) {
                tissue_id = biospecimen_attributes.get(bio_attr);
                break;
            }
        }
        
        HashMap<String, Object> tissue_status_values = new HashMap<>();
        String tissue_status = "";
        if (!tissue_id.trim().equals(""))
            tissue_status = getTissueStatus(tissue_id);
        tissue_status_values.put("value", tissue_status);
        tissue_status_values.put("required", true);
        additional_attributes.put(attributes_prefix+category_separator+"tissue_status", tissue_status_values);
        
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
                for (XMLNode child: root.getChilds()) {
                    searchForAliquots(child);
                }
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
    
    private HashMap<String, HashMap<String, Boolean>> getAdditionalAttributes() {
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
        
        // TO-DO: insert 'exp_data_bed_url' and 'exp_metadata_url'
        // TO-DO: do not insert 'md5sum' -> create a file with all md5sum for each meta and bed files
        
        // other gdc attributes
        attributes.put("cases.submitter_id", false);
        attributes.put("cases.samples.tumor_descriptor", false);
        attributes.put("cases.samples.tissue_type", false);
        //attributes.add("cases.samples.sample_type");
        //attributes.add("cases.samples.submitter_id");
        //attributes.add("cases.samples.sample_id");
        //attributes.add("cases.samples.portions.analytes.aliquots.aliquot_id");
        //attributes.add("cases.samples.portions.analytes.aliquots.submitter_id");
        
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
            if (tissue_id.startsWith("0")) {
                return "tumoral";
            } else if (tissue_id.startsWith("1")) {
                return "normal";
            } else if (tissue_id.startsWith("2")) {
                return "control";
            } else {
                return "undefined";
            }
        } catch (Exception e) {
            return "undefined";
        }
    }
    
}
