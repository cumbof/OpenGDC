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
import opengdc.util.MetadataHandler;
import opengdc.util.XMLNode;

/**
 *
 * @author fabio
 */
public class MetadataParser extends BioParser {

    private final String out_extension = ".META";
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
                    //if (dataType.toLowerCase().trim().equals("clinical supplement")) {
                    if (f.getName().toLowerCase().contains("clinical")) {
                        
                        HashMap<String, String> clinical_metadata_map = MetadataHandler.getDataMap(metadata_from_xml, null);
                        String patient_uuid = MetadataHandler.findKey(clinical_metadata_map, "endsWith", "bcr_patient_uuid");
                        /*try {
                            FileOutputStream fos = new FileOutputStream(outPath + patient_uuid + out_extension);
                            PrintStream out = new PrintStream(fos);
                            for (String attribute: clinical_metadata_map.keySet())
                                out.println(MetadataHandler.getAttributeFromKey(attribute) + "\t" + clinical_metadata_map.get(attribute));
                            out.close();
                            fos.close();
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }*/
                        clinicalBigMap.put(patient_uuid, clinical_metadata_map);
                        
                    }
                    //else if (dataType.toLowerCase().trim().equals("biospecimen supplement")) {
                    else if (f.getName().toLowerCase().contains("biospecimen")) {
                        // TODO: check if meta files contain all attributes
                        
                        XMLNode root = new XMLNode();
                        root.setRoot();
                        root = convertMapToIndexedTree(metadata_from_xml, root);
                        searchForAliquots(root);
                        
                        HashMap<String, HashMap<String, String>> aliquot2attributes = new HashMap<>();
                        for (XMLNode aliquot: aliquotNodes) {
                            // key = node.attribute(uuid)
                            String aliquot_uuid = MetadataHandler.findKey(aliquot.getAttributes(), "endsWith", "bcr_aliquot_uuid");
                            if (!aliquot_uuid.equals("")) {
                                // values = list of node attributes and attributes of parents (from node to root)
                                HashMap<String, String> aliquotMeta = aliquot.getAttributes();
                                HashMap<String, String> parentMeta = extractParentMetadata(aliquot.getParent(), new HashMap<>());
                                aliquotMeta.putAll(parentMeta);
                                aliquot2attributes.put(aliquot_uuid, aliquotMeta);
                            }
                        }
                        
                        if (!aliquot2attributes.isEmpty()) {
                            for (String aliquot_uuid: aliquot2attributes.keySet()) {
                                /*try {
                                    FileOutputStream fos = new FileOutputStream(outPath + aliquot_uuid + out_extension);
                                    PrintStream out = new PrintStream(fos);
                                    for (String attribute: aliquot2attributes.get(aliquot_uuid).keySet())
                                        out.println(MetadataHandler.getAttributeFromKey(attribute) + "\t" + aliquot2attributes.get(aliquot_uuid).get(attribute));
                                    out.close();
                                    fos.close();
                                }
                                catch (Exception e) {
                                    e.printStackTrace();
                                }*/
                                biospecimenBigMap.put(aliquot_uuid, aliquot2attributes.get(aliquot_uuid));
                            }
                        }
                        
                    }
                }
            }
        }
        
        // merge clinical and biospecimen info
        if (!clinicalBigMap.isEmpty() || !biospecimenBigMap.isEmpty()) {
            for (String aliquot_uuid: biospecimenBigMap.keySet()) {
                try {
                    FileOutputStream fos = new FileOutputStream(outPath + aliquot_uuid + out_extension);
                    PrintStream out = new PrintStream(fos);
                    // print biospecimen info
                    String patient_uuid = "";
                    for (String attribute: biospecimenBigMap.get(aliquot_uuid).keySet()) {
                        if (MetadataHandler.getAttributeFromKey(attribute).trim().toLowerCase().equals("shared:bcr_patient_uuid"))
                            patient_uuid = biospecimenBigMap.get(aliquot_uuid).get(attribute);
                        out.println("biospecimen__"+MetadataHandler.getAttributeFromKey(attribute) + "\t" + biospecimenBigMap.get(aliquot_uuid).get(attribute));
                    }
                    // print clinical info
                    if (!patient_uuid.equals("")) {
                        if (clinicalBigMap.containsKey(patient_uuid)) {
                            for (String attribute: clinicalBigMap.get(patient_uuid).keySet())
                                out.println("clinical__"+MetadataHandler.getAttributeFromKey(attribute) + "\t" + clinicalBigMap.get(patient_uuid).get(attribute));
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
    
    public HashMap<String, String> extractParentMetadata(XMLNode node, HashMap<String, String> meta) {
        if (meta == null)
            meta = new HashMap<>();
        meta.putAll(node.getAttributes());
        
        if (node.getParent() == null)
            return meta;
        return extractParentMetadata(node.getParent(), meta);
    }
    
    public void searchForAliquots(XMLNode root) {
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
    
    public XMLNode convertMapToIndexedTree(HashMap<String, Object> map, XMLNode root) {
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
