/*
 * Application: OpenGDC
 * Version: 1.0
 * Authors: Fabio Cumbo (1,2), Eleonora Cappelli (1,2), Emanuel Weitschek (1,3)
 * Organizations: 
 * 1. Institute for Systems Analysis and Computer Science "Antonio Ruberti" - National Research Council of Italy, Rome, Italy
 * 2. Department of Engineering - Third University of Rome, Rome, Italy
 * 3. Department of Engineering - Uninettuno International University, Rome, Italy
 */
package opengdc.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author fabio
 */
public class MetadataHandler {
    
    // for createMap function only
    private static int keyPrefixCounterForUniqueness = 0;
    // cast to HashMap<String, Object>
    private static Object createMap(Node source, boolean restartKeyPrefixCounter) {
        if (restartKeyPrefixCounter)
            keyPrefixCounterForUniqueness = 0;
        HashMap<String, Object> tmpMap = new HashMap<>();
        try {
            int childs = source.getChildNodes().getLength();        
            if (childs == 0) {
                if (source.getTextContent().trim().equals(""))
                    return "NA";
                return source.getTextContent();
            }
            else {
                HashMap<String, Object> dataTmp = new HashMap<>();
                for (int i=0; i<childs; i++) {
                    Node child = source.getChildNodes().item(i);
                    Object child_data = createMap(child, false);
                    if (child.getNodeName().toLowerCase().trim().contains("#text") && childs<=1) {
                        return (String)child_data;
                    }
                    else {
                        if (!child.getNodeName().toLowerCase().trim().contains("#text")) {
                            dataTmp.put(keyPrefixCounterForUniqueness + "_" + child.getNodeName(), child_data);
                            keyPrefixCounterForUniqueness++;
                        }
                    }
                }
                tmpMap.put(keyPrefixCounterForUniqueness + "_" + source.getNodeName(), dataTmp);
                keyPrefixCounterForUniqueness++;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return tmpMap;
    }
    
    /*private static void printMap(Object map, int level) {
        String level_tab = "";
        for (int i=0; i<level; i++)
            level_tab += "\t";
        
        HashMap<String, Object> hash_map = (HashMap<String, Object>)map;
        for (String k: hash_map.keySet()) {
            if (hash_map.get(k) instanceof HashMap) {
                HashMap<String, Object> values = (HashMap<String, Object>)hash_map.get(k);
                System.err.println(level_tab + k);
                if (!values.isEmpty())
                    printMap(values, level+1);
            }
            else if (hash_map.get(k) instanceof String) {
                String value = (String)hash_map.get(k);
                System.err.println(level_tab + k + "\t" + value);
            }
        }
    }*/
    
    public static HashMap<String, Object> getXMLMap(String file_path) {
        HashMap<String, Object> result = new HashMap<>();
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(new File(file_path));
            // normalize text representation
            doc.getDocumentElement().normalize();
            //System.out.println ("Root element of the doc is " + doc.getDocumentElement().getNodeName());
            NodeList roots = doc.getChildNodes();
            //System.err.println(roots.getLength());

            for (int i = 0; i < roots.getLength(); i++) {
                Node node = roots.item(i);
                result = (HashMap<String, Object>)createMap(node, true);
                break;
            }
        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line " + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());
        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();
        } catch (ParserConfigurationException | IOException | DOMException t) {
            t.printStackTrace();
        }

        return result;
    }
    
    public static HashMap<String, String> getDataMap(HashMap<String, Object> map, HashMap<String, String> dataMap) {
        if (dataMap == null)
            dataMap = new HashMap<>();
        for (String k: map.keySet()) {
            if (map.get(k) instanceof HashMap) {
                HashMap<String, Object> values = (HashMap<String, Object>)map.get(k);
                if (!values.isEmpty())
                    getDataMap(values, dataMap);
            }
            else if (map.get(k) instanceof String) {
                String value = (String)map.get(k);
                dataMap.put(k, value);
            }
        }
        return dataMap;
    }
    
    public static String findKey(HashMap<String, String> map, String searchCondition, String str) {
        String value = "";
        if (!map.isEmpty()) {
            for (String k: map.keySet()) {
                if (searchCondition.toLowerCase().trim().equals("endswith")) {
                    if (k.toLowerCase().trim().endsWith(str)) {
                        value = map.get(k);
                        break;
                    }
                }
                else if (searchCondition.toLowerCase().trim().equals("startswith")) {
                    if (getAttributeFromKey(k).toLowerCase().trim().startsWith(str)) {
                        value = map.get(k);
                        break;
                    }
                }
                else if (searchCondition.toLowerCase().trim().equals("contains")) {
                    if (k.toLowerCase().trim().contains(str)) {
                        value = map.get(k);
                        break;
                    }
                }
                else if (searchCondition.toLowerCase().trim().equals("equals")) {
                    if (getAttributeFromKey(k).toLowerCase().trim().equals(str)) {
                        value = map.get(k);
                        break;
                    }
                }
            }
        }
        return value;
    }
    
    public static String getAttributeFromKey(String key) {
        String[] key_split = key.split("_");
        return key.substring(key_split[0].length()+1, key.length());
    }
    
    /*public static void main(String[] args) {
        System.err.println("Biospecimen sample");
        String biospecimen_xml_path = "/Users/fabio/Downloads/test_gdc_download/ACC-biospecimen/nationwidechildrens.org_biospecimen.TCGA-OR-A5J1.xml";
        HashMap<String, Object> xml_biospecimen_data = getXMLMap(biospecimen_xml_path);
        HashMap<String, String> biospecimen_data = getDataMap(xml_biospecimen_data, null);
        System.err.println("XML Data size: " + xml_biospecimen_data.size());
        System.err.println("Data size: " + biospecimen_data.size()+"\n");
        printMap(biospecimen_data, 0);
        
        System.err.println("\n#################################################\n");
        
        System.err.println("Clinical sample");
        String clinical_xml_path = "/Users/fabio/Downloads/test_gdc_download/ACC-clinical/nationwidechildrens.org_clinical.TCGA-OR-A5J1.xml";
        HashMap<String, Object> xml_clinical_data = getXMLMap(clinical_xml_path);
        HashMap<String, String> clinical_data = getDataMap(xml_clinical_data, null);
        System.err.println("XML Data size: " + xml_clinical_data.size());
        System.err.println("Data size: " + clinical_data.size()+"\n");
        printMap(clinical_data, 0);
    }*/
    
}
