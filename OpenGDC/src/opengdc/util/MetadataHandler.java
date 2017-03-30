/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
    
    // cast to HashMap<String, Object>
    public static Object createMap(Node source, int keyPrefixCounterForUniqueness) {
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
                    Object child_data = createMap(child, keyPrefixCounterForUniqueness);
                    if (child.getNodeName().toLowerCase().trim().contains("#text") && childs<=1) {
                        return (String)child_data;
                    }
                    else {
                        if (!child.getNodeName().toLowerCase().trim().contains("#text")) {
                            keyPrefixCounterForUniqueness++;
                            dataTmp.put(keyPrefixCounterForUniqueness + "_" + child.getNodeName(), child_data);
                        }
                    }
                }
                keyPrefixCounterForUniqueness++;
                tmpMap.put(keyPrefixCounterForUniqueness + "_" + source.getNodeName(), dataTmp);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return tmpMap;
    }
    
    public static void printMap(Object map, int level) {
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
    }
    
    public static HashMap<String, Object> getData(String file_path) {
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
                result = (HashMap<String, Object>)createMap(node, 0);
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
    
    /*public static void main(String[] args) {
        System.err.println("Biospecimen sample\n");
        String biospecimen_xml_path = "/Users/fabio/Downloads/test_gdc_download/ACC-biospecimen/nationwidechildrens.org_biospecimen.TCGA-OR-A5J1.xml";
        HashMap<String, Object> biospecimen_data = getData(biospecimen_xml_path);
        printMap(biospecimen_data, 0);
        
        System.err.println("#################################################");
        
        System.err.println("Clinical sample\n");
        String clinical_xml_path = "/Users/fabio/Downloads/test_gdc_download/ACC-clinical/nationwidechildrens.org_clinical.TCGA-OR-A5J1.xml";
        HashMap<String, Object> clinical_data = getData(clinical_xml_path);
        printMap(clinical_data, 0);
    }*/
    
}
