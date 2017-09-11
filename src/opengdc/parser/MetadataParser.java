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
            HashMap<String, HashSet<String>> additional_attributes = getAdditionalAttributes();
            for (String aliquot_uuid: biospecimenBigMap.keySet()) {
                try {
                    FileOutputStream fos = new FileOutputStream(outPath + aliquot_uuid.toLowerCase() + "." + this.getFormat());
                    PrintStream out = new PrintStream(fos);
                    // print biospecimen info
                    String patient_uuid = "";
                    for (String attribute: biospecimenBigMap.get(aliquot_uuid).keySet()) {
                        if (MetadataHandler.getAttributeFromKey(attribute).trim().toLowerCase().equals("shared:bcr_patient_uuid"))
                            patient_uuid = biospecimenBigMap.get(aliquot_uuid).get(attribute);
                        String attribute_parsed = FSUtils.stringToValidJavaIdentifier("biospecimen__" + MetadataHandler.getAttributeFromKey(attribute).replaceAll(":","__"));
                        out.println(attribute_parsed + "\t" + checkForNAs(biospecimenBigMap.get(aliquot_uuid).get(attribute)));
                    }
                    // print clinical info
                    if (!patient_uuid.equals("")) {
                        if (clinicalBigMap.containsKey(patient_uuid)) {
                            for (String attribute: clinicalBigMap.get(patient_uuid).keySet()) {
                                String attribute_parsed = FSUtils.stringToValidJavaIdentifier("clinical__" + MetadataHandler.getAttributeFromKey(attribute).replaceAll(":", "__"));
                                out.println(attribute_parsed + "\t" + checkForNAs(clinicalBigMap.get(patient_uuid).get(attribute)));
                            }
                        }
                    }
                    // print additional metadata
                    if (!additional_attributes.isEmpty()) {
                        for (String metakey: additional_attributes.keySet()) {
                            HashMap<String, String> file_info = GDCQuery.retrieveExpInfoFromAttribute("cases.samples.portions.analytes.aliquots.aliquot_id", aliquot_uuid.toLowerCase(), additional_attributes.get(metakey));
                            if (file_info != null) {
                                for (String attribute: file_info.keySet()) {
                                    String attribute_parsed = FSUtils.stringToValidJavaIdentifier(metakey + "__" + attribute.replaceAll(":", "__"));
                                    out.println(attribute_parsed + "\t" + checkForNAs(file_info.get(attribute)));
                                }
                            }
                        }
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
        if (metaValue.trim().toLowerCase().equals("na"))
            return "";
        else return metaValue;
    }
    
    private HashMap<String, HashSet<String>> getAdditionalAttributes() {
        HashMap<String, HashSet<String>> additionalAttributes = new HashMap<>();
        HashSet<String> attributes = new HashSet<>();
        attributes.add("data_category");
        attributes.add("data_format");
        attributes.add("data_type");
        attributes.add("experimental_strategy");
        attributes.add("file_id");
        attributes.add("file_name");
        attributes.add("file_size");
        attributes.add("platform");
        attributes.add("analysis.analysis_id");
        attributes.add("analysis.workflow_link");
        attributes.add("analysis.workflow_type");
        attributes.add("cases.case_id");
        attributes.add("cases.disease_type");
        attributes.add("cases.primary_site");
        attributes.add("cases.demographic.year_of_birth");
        attributes.add("cases.project.program.program_id");
        attributes.add("cases.project.program.name");
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
    
}
